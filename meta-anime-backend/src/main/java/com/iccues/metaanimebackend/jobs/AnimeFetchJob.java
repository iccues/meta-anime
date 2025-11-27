package com.iccues.metaanimebackend.jobs;

import com.iccues.metaanimebackend.service.MappingSyncService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AnimeFetchJob {

    @Resource
    MappingSyncService mappingSyncService;

    @Scheduled(cron = "0 0 4 * * *")
    public void fetchAnime() {
        mappingSyncService.syncMappings();
    }

    @PostConstruct
    public void init() {
        fetchAnime();
    }
}
