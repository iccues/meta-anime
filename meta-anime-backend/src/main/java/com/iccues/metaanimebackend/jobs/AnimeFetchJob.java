package com.iccues.metaanimebackend.jobs;

import com.iccues.metaanimebackend.service.AniListFetchService;
import com.iccues.metaanimebackend.service.BangumiFetchService;
import com.iccues.metaanimebackend.service.ScoreService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AnimeFetchJob {

    @Resource
    BangumiFetchService bangumiFetchService;
    @Resource
    AniListFetchService aniListFetchService;

    @Resource
    ScoreService scoreService;

    @Scheduled(cron = "0 0 4 * * *")
    public void fetchAnime() {
        bangumiFetchService.fetchAnime(2025, 10);
        aniListFetchService.fetchAnime(2025, "FALL");
        scoreService.calculateAverageScore();
    }

    @PostConstruct
    public void init() {
        fetchAnime();
    }
}
