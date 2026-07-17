package com.yukiani.server.controller;

import com.yukiani.server.common.Response;
import com.yukiani.server.service.MetricRecalculationService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供管理端低频数据维护 API。
 */
@RestController
@RequestMapping("/api/admin")
public class AdminMaintenanceController {

    @Resource
    MetricRecalculationService metricRecalculationService;

    /**
     * 异步重新计算全部 Mapping 归一化指标和 Anime 聚合指标。
     *
     * @return 任务已启动时返回 202；已有任务运行时返回 409
     */
    @PostMapping("/recalculate_metrics")
    public ResponseEntity<Response<String>> recalculateMetrics() {
        if (!metricRecalculationService.tryStart()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Response.fail("TASK_RUNNING", "指标重算任务正在运行"));
        }

        metricRecalculationService.recalculateAllMetrics();
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Response.ok("指标重算任务已启动"));
    }
}
