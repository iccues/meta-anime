package com.yukiani.server.controller;

import com.yukiani.server.common.Response;
import com.yukiani.server.dto.admin.AdminMappingDTO;
import com.yukiani.server.dto.admin.CreateMappingRequest;
import com.yukiani.server.dto.admin.UpdateMappingAnimeRequest;
import com.yukiani.server.entity.Mapping;
import com.yukiani.server.mapper.AdminAnimeMapper;
import com.yukiani.server.service.MappingManageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 提供管理端外部平台 Mapping 的查询与维护 API。
 */
@Controller
@RequestMapping("/api/admin")
public class AdminMappingController {

    @Resource
    MappingManageService mappingManageService;

    @Resource
    AdminAnimeMapper adminAnimeMapper;

    /**
     * 查询尚未关联动画的平台 Mapping。
     *
     * @return 包含未关联 Mapping 列表的成功响应；无结果时列表为空
     */
    @ResponseBody
    @GetMapping("/get_unmapped_mapping_list")
    public Response<List<AdminMappingDTO>> getUnmappedMappingList() {
        List<Mapping> mappingList = mappingManageService.getUnmappedMappingList();
        List<AdminMappingDTO> mappingDTOList = adminAnimeMapper.toMappingDtoList(mappingList);
        return Response.ok(mappingDTOList);
    }

    /**
     * 更新 Mapping 的动画关联；request.animeId 为空时解除关联。
     *
     * @param request 包含 mappingId 和 animeId；animeId 为 {@code null} 时解除关联
     * @return 包含更新后 Mapping 的成功响应
     */
    @ResponseBody
    @PutMapping("/update_mapping_anime")
    public Response<AdminMappingDTO> updateMappingAnime(@RequestBody UpdateMappingAnimeRequest request) {
        Mapping savedMapping = mappingManageService.updateMappingAnime(request.mappingId(), request.animeId());
        AdminMappingDTO mappingDTO = adminAnimeMapper.toMappingDto(savedMapping);
        return Response.ok(mappingDTO);
    }

    /**
     * 删除指定平台 Mapping 并更新原动画的聚合指标。
     *
     * @param mappingId 待删除 Mapping 的 mappingId
     * @return 不包含响应数据的成功响应
     */
    @ResponseBody
    @DeleteMapping("/delete_mapping/{mappingId}")
    public Response<Void> deleteMapping(@PathVariable Long mappingId) {
        mappingManageService.deleteMapping(mappingId);
        return Response.ok(null);
    }

    /**
     * 从指定平台即时抓取并创建 Mapping。
     *
     * @param request 包含 sourcePlatform 和 platformId
     * @return 包含已创建 Mapping 的成功响应
     */
    @ResponseBody
    @PostMapping("/create_mapping")
    public Response<AdminMappingDTO> createMapping(@RequestBody CreateMappingRequest request) {
        Mapping mapping = mappingManageService.createMapping(request.sourcePlatform(), request.platformId());
        AdminMappingDTO mappingDTO = adminAnimeMapper.toMappingDto(mapping);
        return Response.ok(mappingDTO);
    }
}
