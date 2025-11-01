package com.iccues.metaanimebackend.jobs;

import com.iccues.metaanimebackend.service.BangumiFetchService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BangumiFetchJob {

    @Resource
    BangumiFetchService bangumiFetchService;

    @Scheduled(cron = "0 0 4 * * *")
    public void scheduledFetch() {
        bangumiFetchService.fetchAnime();
    }

    @PostConstruct
    public void init() {
        scheduledFetch();
    }
}
