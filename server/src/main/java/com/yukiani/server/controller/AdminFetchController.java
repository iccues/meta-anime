package com.yukiani.server.controller;

import com.yukiani.server.common.Response;
import com.yukiani.server.entity.Platform;
import com.yukiani.server.entity.Season;
import com.yukiani.server.service.fetch.FetchService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 提供管理端手动触发数据抓取、关联和指标计算的 API。
 */
@Controller
@RequestMapping("/api/admin/fetch")
public class AdminFetchController {

    @Resource
    FetchService fetchService;

    /**
     * 异步抓取平台 Mapping 并关联到动画。
     *
     * @param year     开播年份
     * @param season   开播季度；为空时抓取全年
     * @param platform 目标平台；为空时抓取全部平台
     * @return 包含任务已启动提示的成功响应
     */
    @ResponseBody
    @PostMapping("/anime")
    public Response<String> fetchAnime(@RequestParam int year, Season season, Platform platform) {
        fetchService.fetchAnime(year, season, platform);
        return Response.ok("数据抓取任务已启动");
    }

    /**
     * 异步抓取并保存平台 Mapping。
     *
     * @param year     开播年份
     * @param season   开播季度；为空时抓取全年
     * @param platform 目标平台；为空时抓取全部平台
     * @return 包含任务已启动提示的成功响应
     */
    @ResponseBody
    @PostMapping("/mapping")
    public Response<String> fetchMapping(@RequestParam int year, Season season, Platform platform) {
        fetchService.fetchMapping(year, season, platform);
        return Response.ok("映射抓取任务已启动");
    }

    /**
     * 异步关联当前所有孤立 Mapping。
     *
     * @return 包含任务已启动提示的成功响应
     */
    @ResponseBody
    @PostMapping("/link")
    public Response<String> linkMappings() {
        fetchService.linkMappings();
        return Response.ok("映射合并任务已启动");
    }

    /**
     * 异步重新计算全部动画的综合指标。
     *
     * @return 包含任务已启动提示的成功响应
     */
    @ResponseBody
    @PostMapping("/calculate_metric")
    public Response<String> calculateMetric() {
        fetchService.calculateAllMetric();
        return Response.ok("评分计算任务已启动");
    }
}
