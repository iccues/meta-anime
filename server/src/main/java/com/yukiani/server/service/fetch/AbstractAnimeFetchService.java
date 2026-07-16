package com.yukiani.server.service.fetch;

import com.fasterxml.jackson.databind.JsonNode;
import com.yukiani.server.config.PlatformConfig;
import com.yukiani.server.config.PlatformConfigProperties;
import com.yukiani.server.entity.*;
import com.yukiani.server.exception.FetchFailedException;
import com.yukiani.server.repo.MappingRepository;
import com.yukiani.server.service.MappingRepoService;
import com.yukiani.server.service.MetricService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.List;

/**
 * 定义外部平台动画数据的抓取、字段提取、指标归一化和持久化模板。
 *
 * <p>平台实现只负责 API 调用与 field mapping，公共流程负责构造并幂等保存 Mapping。</p>
 */
@Slf4j
public abstract class AbstractAnimeFetchService {
    @Resource
    protected MappingRepoService mappingRepoService;

    @Resource
    protected MappingRepository mappingRepository;
    @Resource
    protected PlatformConfigProperties platformConfigProperties;

    protected abstract Platform getPlatform();

    /**
     * 提取开播日期。
     *
     * @return 平台数据中的开播日期；缺失或无效时返回 {@code null}
     */
    protected abstract LocalDate extractStartDate(JsonNode jsonNode);

    protected abstract AnimeTitles extractTitles(JsonNode jsonNode);

    /**
     * 提取封面地址。
     *
     * @return 平台数据中的封面地址；缺失时返回 {@code null}
     */
    protected abstract String extractCoverImage(JsonNode jsonNode);

    /**
     * 提取平台侧 platformId。
     *
     * @return platformId；缺失时返回 {@code null}
     */
    protected abstract String extractPlatformId(JsonNode jsonNode);

    /**
     * 提取平台原始评分。
     *
     * @return 平台原始评分；无有效评分时返回 {@code null}
     */
    protected abstract Double extractRawScore(JsonNode jsonNode);

    /**
     * 使用平台均值和标准差将评分映射到以 50 为中心的统一尺度。
     *
     * @return 归一化评分；原始评分无效时返回 {@code null}
     */
    public Double normalizeScore(Double rawScore) {
        if (rawScore == null || rawScore <= 0) {
            return null;
        }
        PlatformConfig config = platformConfigProperties.getConfig(getPlatform());
        double mean = config.getScoreMean();
        double std = config.getScoreStd();
        double z = (rawScore - mean) / std;
        return 50 + (z * (100 / 6.0));
    }

    /**
     * 提取平台原始热度。
     *
     * @return 平台原始热度；缺失时由平台实现返回零
     */
    protected abstract double extractRawPopularity(JsonNode jsonNode);

    /**
     * 使用平台热度中位数将原始热度缩放到统一尺度。
     */
    public double normalizePopularity(double rawPopularity) {
        PlatformConfig config = platformConfigProperties.getConfig(getPlatform());
        double median = config.getPopularityMedian();
        return rawPopularity / median * 10000;
    }

    protected MappingInfo extractMappingInfo(JsonNode jsonNode) {
        return new MappingInfo(
                extractTitles(jsonNode),
                extractCoverImage(jsonNode),
                extractStartDate(jsonNode)
        );
    }

    /**
     * 抓取指定年份和季度的全部平台数据。
     *
     * @param season 开播季度；为空时抓取全年
     */
    protected abstract List<JsonNode> fetchMappingJson(int year, Season season);

    /**
     * 按 platformId 抓取单条数据。
     *
     * @return 平台原始数据；平台未返回内容时返回 {@code null}
     */
    protected abstract JsonNode fetchSingleMappingJson(String platformId);

    /**
     * 提取、归一化并幂等保存单条平台数据。
     *
     * <p>缺少 platformId 或开播日期的数据不会进入主数据关联流程。</p>
     */
    void processAndSaveMapping(JsonNode jsonNode) {
        String platformId = extractPlatformId(jsonNode);
        MappingInfo mappingInfo = extractMappingInfo(jsonNode);
        if (platformId == null || mappingInfo.getStartDate() == null) return;

        Mapping mapping = new Mapping(getPlatform(), platformId, mappingInfo);

        Double rawScore = extractRawScore(jsonNode);
        Double normalizedScore = normalizeScore(rawScore);
        mapping.setRawScore(rawScore);
        mapping.setNormalizedScore(normalizedScore);

        double rawPopularity = extractRawPopularity(jsonNode);
        double normalizedPopularity = normalizePopularity(rawPopularity);
        mapping.setRawPopularity(rawPopularity);
        mapping.setNormalizedPopularity(normalizedPopularity);

        mappingRepoService.saveOrUpdate(mapping);
    }

    /**
     * 抓取并保存指定年份和季度的全部平台 Mapping。
     *
     * @param season 开播季度；为空时抓取全年
     * @throws FetchFailedException 平台请求或数据处理失败时抛出
     */
    @Transactional
    public void fetchAndSaveMappings(int year, Season season) {
        try {
            List<JsonNode> mediaList = fetchMappingJson(year, season);
            for (JsonNode jsonNode : mediaList) {
                processAndSaveMapping(jsonNode);
            }
        } catch (WebClientResponseException e) {
            log.warn("Failed to fetch mappings from {} for year={}, season={}: {} {}",
                    getPlatform(), year, season, e.getStatusCode(), e.getResponseBodyAsString());
            throw new FetchFailedException(getPlatform(),
                    String.format("year=%d, season=%s", year, season));
        } catch (Exception e) {
            log.error("Unexpected error fetching mappings from {} for year={}, season={}: {}",
                    getPlatform(), year, season, e.getMessage());
            throw new FetchFailedException(getPlatform(),
                    String.format("year=%d, season=%s", year, season));
        }
    }

    /**
     * 抓取并保存指定平台资源。
     *
     * @throws FetchFailedException 平台请求、数据处理或结果读取失败时抛出
     */
    @Transactional
    public Mapping fetchAndSaveMapping(String platformId) {
        try {
            JsonNode jsonNode = fetchSingleMappingJson(platformId);
            if (jsonNode == null) {
                log.warn("{} API returned null for platformId={}", getPlatform(), platformId);
                throw new FetchFailedException(getPlatform(), platformId);
            }
            processAndSaveMapping(jsonNode);
            return mappingRepository.findBySourcePlatformAndPlatformId(getPlatform(), platformId);
        } catch (WebClientResponseException e) {
            log.warn("Failed to fetch mapping from {} for platformId={}: {} {}",
                    getPlatform(), platformId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new FetchFailedException(getPlatform(), platformId);
        } catch (FetchFailedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error fetching mapping from {} for platformId={}: {}",
                    getPlatform(), platformId, e.getMessage());
            throw new FetchFailedException(getPlatform(), platformId);
        }
    }
}
