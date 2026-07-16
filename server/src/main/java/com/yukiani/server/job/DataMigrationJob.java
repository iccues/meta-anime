package com.yukiani.server.job;

import com.yukiani.server.entity.Mapping;
import com.yukiani.server.entity.Platform;
import com.yukiani.server.repo.MappingRepository;
import com.yukiani.server.service.fetch.AbstractAnimeFetchService;
import com.yukiani.server.service.fetch.FetchService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 在平台归一化配置变更后，批量重算历史 Mapping 数据。
 *
 * <p>修改 {@code application.yml} 中的 {@code popularity-median}、
 * {@code score-mean} 或 {@code score-std} 后，可临时启用对应 method 的
 * {@code @PostConstruct}，在应用启动时使用新参数执行 migration。
 * 执行完成后应立即重新注释，避免后续启动时重复执行。</p>
 *
 * <p>任务会遍历并更新全部 Mapping，执行前应确认数据量并做好数据库备份。</p>
 */
@Slf4j
@Service
public class DataMigrationJob {
    @Resource
    MappingRepository mappingRepository;
    @Resource
    FetchService fetchService;

    /**
     * 根据当前 {@code popularity-median}，从 {@code rawPopularity}
     * 重新计算所有 Mapping 的 {@code normalizedPopularity}；原始值为空时跳过。
     */
    // @PostConstruct
    @Transactional
    public void recalculateAllPopularity() {
        log.info("开始重新计算所有 Mapping 的 normalizedPopularity...");

        List<Mapping> mappingList = mappingRepository.findAll();
        int total = mappingList.size();
        int updated = 0;

        for (Mapping mapping : mappingList) {
            Platform platform = mapping.getSourcePlatform();
            AbstractAnimeFetchService implFetchService = fetchService.getFetchService(platform);
            Double raw = mapping.getRawPopularity();

            if (raw == null) {
                continue;
            }

            Double normalized = implFetchService.normalizePopularity(raw);
            mapping.setNormalizedPopularity(normalized);
            mappingRepository.save(mapping);
            updated++;
        }

        log.info("重新计算 popularity 完成！总计: {}, 更新: {}", total, updated);
    }

    /**
     * 根据当前 {@code score-mean} 和 {@code score-std}，从 {@code rawScore}
     * 重新计算所有 Mapping 的 {@code normalizedScore}；原始值无效时清空归一化结果。
     */
    // @PostConstruct
    @Transactional
    public void recalculateAllScores() {
        log.info("开始重新计算所有 Mapping 的 normalizedScore...");

        List<Mapping> mappingList = mappingRepository.findAll();
        int total = mappingList.size();
        int updated = 0;

        for (Mapping mapping : mappingList) {
            Platform platform = mapping.getSourcePlatform();
            AbstractAnimeFetchService implFetchService = fetchService.getFetchService(platform);
            Double rawScore = mapping.getRawScore();

            if (rawScore == null || rawScore <= 0) {
                mapping.setNormalizedScore(null);
                continue;
            }

            Double normalizedScore = implFetchService.normalizeScore(rawScore);
            mapping.setNormalizedScore(normalizedScore);
            mappingRepository.save(mapping);
            updated++;
        }

        log.info("重新计算 score 完成！总计: {}, 更新: {}", total, updated);
    }
}
