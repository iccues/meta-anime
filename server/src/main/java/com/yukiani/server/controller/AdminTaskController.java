package com.yukiani.server.controller;

import com.yukiani.server.common.Response;
import com.yukiani.server.entity.Platform;
import com.yukiani.server.entity.Season;
import com.yukiani.server.service.MetricRecalculationService;
import com.yukiani.server.service.fetch.FetchService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供管理端异步数据抓取、关联和维护任务 API。
 */
@RestController
@RequestMapping("/api/admin/tasks")
public class AdminTaskController {

    @Resource
    FetchService fetchService;

    @Resource
    MetricRecalculationService metricRecalculationService;

    /**
     * 异步抓取平台 Mapping 并关联到动画。
     */
    @PostMapping("/anime-fetch")
    public Response<String> fetchAnime(
            @RequestParam int year,
            @RequestParam(required = false) Season season,
            @RequestParam(required = false) Platform platform) {
        fetchService.fetchAnime(year, season, platform);
        return Response.ok("数据抓取任务已启动");
    }

    /**
     * 异步抓取并保存平台 Mapping。
     */
    @PostMapping("/mapping-fetch")
    public Response<String> fetchMapping(
            @RequestParam int year,
            @RequestParam(required = false) Season season,
            @RequestParam(required = false) Platform platform) {
        fetchService.fetchMapping(year, season, platform);
        return Response.ok("映射抓取任务已启动");
    }

    /**
     * 异步关联当前所有孤立 Mapping。
     */
    @PostMapping("/mapping-link")
    public Response<String> linkMappings() {
        fetchService.linkMappings();
        return Response.ok("映射合并任务已启动");
    }

    /**
     * 异步重新计算全部 Mapping 归一化指标和 Anime 聚合指标。
     *
     * <p>仅表示任务已提交；是否真正开始执行、以及执行结果均由服务端日志记录，已有任务运行时会被跳过。</p>
     */
    @PostMapping("/metric-recalculation")
    public ResponseEntity<Response<String>> recalculateMetrics() {
        metricRecalculationService.recalculateAllMetrics();
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Response.ok("指标重算任务已启动"));
    }
}
