package com.yukiani.server.controller;

import com.yukiani.server.common.Response;
import com.yukiani.server.dto.admin.AdminAnimeDTO;
import com.yukiani.server.dto.admin.AnimeCreateRequest;
import com.yukiani.server.dto.admin.AnimeUpdateRequest;
import com.yukiani.server.entity.Anime;
import com.yukiani.server.entity.ReviewStatus;
import com.yukiani.server.entity.Season;
import com.yukiani.server.mapper.AdminAnimeMapper;
import com.yukiani.server.service.AnimeManageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 提供管理端动画主数据的查询与维护 API。
 */
@Controller
@RequestMapping("/api/admin")
public class AdminAnimeController {

    @Resource
    AnimeManageService animeManageService;

    @Resource
    AdminAnimeMapper adminAnimeMapper;

    /**
     * 按审核状态和季度筛选动画。
     *
     * @param reviewStatus 审核状态；为空时不过滤
     * @param year         开播年份；为空时不过滤日期
     * @param season       开播季度；为空时查询全年
     * @return 包含符合条件动画列表的成功响应；无结果时列表为空
     */
    @ResponseBody
    @GetMapping("/get_anime_list")
    public Response<List<AdminAnimeDTO>> getAnimeList(
            @RequestParam(required = false) ReviewStatus reviewStatus,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Season season) {
        List<Anime> animeList = animeManageService.getAnimeList(reviewStatus, year, season);
        List<AdminAnimeDTO> animeDtoList = adminAnimeMapper.toAnimeDtoList(animeList);
        return Response.ok(animeDtoList);
    }

    /**
     * 创建动画主数据。
     *
     * @param request 通过参数校验的动画基础信息
     * @return 包含已创建动画的成功响应
     */
    @ResponseBody
    @PostMapping("/create_anime")
    public Response<AdminAnimeDTO> createAnime(@Valid @RequestBody AnimeCreateRequest request) {
        Anime anime = adminAnimeMapper.requestToAnime(request);
        Anime savedAnime = animeManageService.createAnime(anime);
        AdminAnimeDTO animeDto = adminAnimeMapper.toAnimeDto(savedAnime);
        return Response.ok(animeDto);
    }

    /**
     * 更新动画主数据，未提供的字段保持原值。
     *
     * @param request animeId 及待更新字段
     * @return 包含更新后动画的成功响应
     */
    @ResponseBody
    @PutMapping("/update_anime")
    public Response<AdminAnimeDTO> updateAnime(@Valid @RequestBody AnimeUpdateRequest request) {
        Anime updatedAnime = animeManageService.updateAnime(request.animeId(),
                anime -> adminAnimeMapper.updateAnimeByRequest(request, anime));
        AdminAnimeDTO animeDto = adminAnimeMapper.toAnimeDto(updatedAnime);
        return Response.ok(animeDto);
    }

    /**
     * 删除指定动画并解除其平台 Mapping。
     *
     * @param animeId 待删除动画的 animeId
     * @return 不包含响应数据的成功响应
     */
    @ResponseBody
    @DeleteMapping("/delete_anime/{animeId}")
    public Response<Void> deleteAnime(@PathVariable Long animeId) {
        animeManageService.deleteAnime(animeId);
        return Response.ok(null);
    }

    /**
     * 删除所有未审核通过的动画及因此产生的孤立 Mapping。
     *
     * @return 不包含响应数据的成功响应
     */
    @ResponseBody
    @DeleteMapping("/delete_non_approved_animes")
    public Response<Void> deleteNonApprovedAnimes() {
        animeManageService.deleteNonApprovedAnimes();
        return Response.ok(null);
    }
}
