package com.yukiani.server.dto.admin;

/**
 * 更新 Mapping 与动画关联的 Request。
 *
 * @param animeId 目标动画的 animeId，传入 {@code null} 表示解除关联
 */
public record UpdateMappingAnimeRequest(
        Long mappingId,
        Long animeId
) {
}
