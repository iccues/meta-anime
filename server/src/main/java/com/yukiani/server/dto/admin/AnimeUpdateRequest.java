package com.yukiani.server.dto.admin;

import com.yukiani.server.entity.AnimeTitles;
import com.yukiani.server.entity.ReviewStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * 管理端更新动画的请求参数，未提供的字段保持原值。
 */
public record AnimeUpdateRequest(
        @NotNull
        Long animeId,
        AnimeTitles title,
        String coverImage,
        ReviewStatus reviewStatus,
        LocalDate startDate
) {
}
