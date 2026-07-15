package com.yukiani.server.scheduler;

import com.yukiani.server.service.MappingSyncService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 按固定周期收集并刷新需要同步的外部平台 Mapping。
 */
@Component
@Slf4j
public class MappingSyncScheduler {

    @Resource
    MappingSyncService mappingSyncService;

    /** 每天凌晨 4 点重新收集待同步 Mapping。 */
    @Scheduled(cron = "0 0 4 * * ?")
    public void scheduleDailyMappingCollection() {
        log.info("=== Daily mapping collection started ===");
        try {
            mappingSyncService.collectMappingsForSync();
            log.info("=== Daily mapping collection completed ===");
        } catch (Exception e) {
            log.error("=== Daily mapping collection failed: {} ===", e.getMessage());
        }
    }

    /** 每 6 小时同步一次待处理 Mapping。 */
    @Scheduled(cron = "0 0 */6 * * ?")
    public void scheduleMappingSync() {
        log.info("=== Periodic mapping sync started ===");
        try {
            mappingSyncService.processPendingMappings();
            log.info("=== Periodic mapping sync completed ===");
        } catch (Exception e) {
            log.error("=== Periodic mapping sync failed: {} ===", e.getMessage());
        }
    }

    /**
     * 收集 Mapping 并触发首次同步；仅在需要启动时同步时手动启用 {@code @PostConstruct}。
     */
    // @PostConstruct
    public void init() {
        log.info("Initializing mapping sync scheduler");
        try {
            mappingSyncService.collectMappingsForSync();
            mappingSyncService.processPendingMappings();
            log.info("Mapping sync scheduler initialized");
        } catch (Exception e) {
            log.error("Failed to initialize mapping sync scheduler: {}", e.getMessage());
        }
    }
}
