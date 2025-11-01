package com.iccues.metaanimebackend.jobs;

import com.iccues.metaanimebackend.service.AniListFetchService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AniListFetchJob {

    @Resource
    private AniListFetchService aniListFetchService;

    @Scheduled(cron = "0 0 4 * * *")
    public void scheduledFetch() {
        aniListFetchService.fetchAnime(2025, "FALL");
    }

    @PostConstruct
    public void init() {
        scheduledFetch();
    }
}
