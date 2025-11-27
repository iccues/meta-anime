package com.iccues.metaanimebackend.service;

import com.iccues.metaanimebackend.entity.Anime;
import com.iccues.metaanimebackend.entity.Mapping;
import com.iccues.metaanimebackend.entity.ReviewStatus;
import com.iccues.metaanimebackend.repo.AnimeRepository;
import com.iccues.metaanimebackend.service.fetch.AbstractAnimeFetchService;
import com.iccues.metaanimebackend.service.fetch.FetchService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class MappingSyncService {

    @Resource
    AnimeRepository animeRepository;

    @Resource
    FetchService fetchService;

    @Async
    @Transactional
    public void syncMappings() {
        List<Anime> animeList = animeRepository.findAllByReviewStatus(ReviewStatus.APPROVED);
        for (Anime anime : animeList) {
            if (shouldSyncAnime(anime)) {
                anime.getMappings().forEach(this::syncMapping);
            }
        }
    }

    private boolean shouldSyncAnime(Anime anime) {
        if (anime.getStartDate() == null) {
            log.warn("Anime {} has no start date, skipping sync", anime.getAnimeId());
            return false;
        }

        long today = LocalDate.now().toEpochDay();
        long startDate = anime.getStartDate().toEpochDay();
        long daysSinceStart = today - startDate;

        // 同步最近 90 天内开播的动漫（修正了日期计算逻辑）
        return daysSinceStart >= 0 && daysSinceStart < 90;
    }

    private void syncMapping(Mapping mapping) {
        try {
            AbstractAnimeFetchService service = fetchService.getFetchServiceByName(mapping.getSourcePlatform());
            if (service != null) {
                service.fetchAndSaveMapping(mapping.getPlatformId());
                log.info("Successfully synced mapping {} from {}", mapping.getPlatformId(), mapping.getSourcePlatform());
            } else {
                log.warn("No fetch service found for platform: {}", mapping.getSourcePlatform());
            }
        } catch (Exception e) {
            log.error("Failed to sync mapping {} from {}: {}",
                    mapping.getPlatformId(), mapping.getSourcePlatform(), e.getMessage(), e);
        }
    }
}
