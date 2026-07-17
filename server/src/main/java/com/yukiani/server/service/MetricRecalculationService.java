package com.yukiani.server.service;

import com.yukiani.server.entity.Anime;
import com.yukiani.server.entity.Mapping;
import com.yukiani.server.repo.AnimeRepository;
import com.yukiani.server.repo.MappingRepository;
import com.yukiani.server.service.fetch.AbstractAnimeFetchService;
import com.yukiani.server.service.fetch.FetchService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 使用当前平台配置异步重算 Mapping 归一化指标和 Anime 聚合指标。
 *
 * <p>服务由管理 API 触发，并通过进程内执行锁防止重复运行。</p>
 */
@Slf4j
@Service
public class MetricRecalculationService {
    @Resource
    MappingRepository mappingRepository;
    @Resource
    AnimeRepository animeRepository;
    @Resource
    FetchService fetchService;
    @Resource
    MetricService metricService;

    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * 尝试占用任务执行权；已有任务运行时返回 {@code false}。
     */
    public boolean tryStart() {
        return running.compareAndSet(false, true);
    }

    /**
     * 使用当前平台配置重算全部 Mapping 和 Anime 指标，完成后释放执行锁。
     */
    @Async
    @Transactional
    public void recalculateAllMetrics() {
        try {
            log.info("开始重新计算所有 Mapping 的归一化指标...");

            List<Mapping> mappingList = mappingRepository.findAll();
            for (Mapping mapping : mappingList) {
                AbstractAnimeFetchService implFetchService = fetchService.getFetchService(
                        mapping.getSourcePlatform()
                );
                implFetchService.recalculateMetrics(mapping);
            }

            mappingRepository.saveAll(mappingList);
            mappingRepository.flush();
            recalculateAllAnimeMetrics();
            log.info("全部指标重新计算完成！Mapping 总计: {}", mappingList.size());
        } catch (RuntimeException e) {
            log.error("指标重新计算失败", e);
            throw e;
        } finally {
            running.set(false);
        }
    }

    /**
     * 根据当前 Mapping 归一化指标重新计算全部 Anime 聚合指标。
     */
    void recalculateAllAnimeMetrics() {
        List<Anime> animeList = animeRepository.findAll();
        for (Anime anime : animeList) {
            metricService.calculateMetric(anime);
        }
    }
}
