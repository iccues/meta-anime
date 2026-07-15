package com.yukiani.server.service;

import com.yukiani.server.entity.Anime;
import com.yukiani.server.entity.Mapping;
import com.yukiani.server.entity.Platform;
import com.yukiani.server.entity.ReviewStatus;
import com.yukiani.server.repo.AnimeRepository;
import com.yukiani.server.service.fetch.AbstractAnimeFetchService;
import com.yukiani.server.service.fetch.FetchService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 根据动画开播时间维护待同步队列，并并行刷新外部平台 Mapping。
 */
@Service
@Slf4j
public class MappingSyncService {

    @Resource
    AnimeRepository animeRepository;

    @Resource
    FetchService fetchService;

    @Resource
    MetricService metricService;

    /** 线程安全的待同步 Mapping 队列；成功项会移除，失败项保留到下一轮重试。 */
    List<Mapping> pendingMappings = Collections.synchronizedList(new ArrayList<>());

    /**
     * 重新收集需要同步的已审核动画 Mapping，并报告上一批未成功的 Mapping。
     *
     * <p>开播越久同步频率越低，具体频率由 {@link #shouldSyncAnime(Anime)} 决定。</p>
     */
    @Transactional(readOnly = true)
    public void collectMappingsForSync() {
        // 队列中的遗留项代表上一轮处理后仍失败的 Mapping。
        if (!pendingMappings.isEmpty()) {
            log.error("Found {} mapping(s) that failed to sync yesterday", pendingMappings.size());
            // Mapping 明细仅在 DEBUG 级别输出，避免失败量较大时污染常规日志。
            if (log.isDebugEnabled()) {
                pendingMappings.forEach(mapping ->
                    log.debug("Failed mapping: {}:{}", mapping.getSourcePlatform(), mapping.getPlatformId())
                );
            }
        }

        // 以数据库当前状态重建队列，避免重复累积已过期项。
        pendingMappings.clear();

        List<Anime> animeList = animeRepository.findAllByReviewStatus(ReviewStatus.APPROVED);

        // 在只读事务内展开懒加载集合，离开事务后仅处理已收集的 Mapping。
        pendingMappings.addAll(animeList.stream()
                .filter(this::shouldSyncAnime)
                .flatMap(anime -> anime.getMappings().stream())
                .toList());

        log.info("Collected {} new mapping(s) for today", pendingMappings.size());
    }

    /**
     * 异步并行处理当前待同步队列，失败项会保留到下一轮。
     */
    @Async
    public void processPendingMappings() {
        int total = pendingMappings.size();
        long startTime = System.currentTimeMillis();
        
        log.info("Processing {} pending mapping(s)", total);

        List.copyOf(pendingMappings)
                .parallelStream()
                .forEach(this::syncMapping);

        int failed = pendingMappings.size();
        int success = total - failed;
        long duration = System.currentTimeMillis() - startTime;
        
        log.info("Mapping sync completed - Total: {}, Success: {}, Failed: {}, Duration: {}ms", 
            total, success, failed, duration);
    }

    private boolean shouldSyncAnime(Anime anime) {
        if (anime.getStartDate() == null) {
            log.warn("Anime {} has no start date, skipping sync", anime.getAnimeId());
            return false;
        }

        long today = LocalDate.now().toEpochDay();
        long startDate = anime.getStartDate().toEpochDay();
        long daysSinceStart = today - startDate;

        if (daysSinceStart <= 0) {
            return false;
        }
        if (daysSinceStart < 30) {
            return true;
        }
        // 使用日期和 animeId 分片，让不同动画均匀分布到较低频率的同步周期。
        if (daysSinceStart < 90) {
            return today % 2 == anime.getAnimeId() % 2;
        }
        if (daysSinceStart < 180) {
            return today % 7 == anime.getAnimeId() % 7;
        }
        return today % 30 == anime.getAnimeId() % 30;
    }

    /**
     * 同步单个 Mapping；成功时移出队列，失败时保留以供下一轮重试。
     */
    @Transactional
    public void syncMapping(Mapping mapping) {
        Platform platform = mapping.getSourcePlatform();
        String platformId = mapping.getPlatformId();

        try {
            AbstractAnimeFetchService service = fetchService.getFetchService(platform);
            log.debug("Syncing mapping {}:{}", platform, platformId);
            service.fetchAndSaveMapping(platformId);
            pendingMappings.remove(mapping);
            log.debug("Successfully synced mapping {}:{}", platform, platformId);
        } catch (Exception e) {
            log.warn("Failed to sync mapping {}:{} - {}", platform, platformId, e.getMessage());
        }
    }
}
