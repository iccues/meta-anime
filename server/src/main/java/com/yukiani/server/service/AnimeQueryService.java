package com.yukiani.server.service;

import com.yukiani.server.entity.Anime;
import com.yukiani.server.entity.LocalDateRange;
import com.yukiani.server.entity.ReviewStatus;
import com.yukiani.server.entity.Season;
import com.yukiani.server.entity.SortBy;
import com.yukiani.server.repo.AnimeRepository;
import com.yukiani.server.spec.AnimeSpec;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Service;

/**
 * 处理仅面向已审核动画的公开 API 分页查询和标题搜索。
 */
@Service
public class AnimeQueryService {

    @Resource
    AnimeRepository animeRepository;

    @Resource
    SeasonService seasonService;

    /**
     * 分页查询指定年份和季度的已审核动画。
     *
     * @param year       开播年份；为空时不过滤日期
     * @param season     开播季度；为空时查询全年
     * @param pageSize   每页数量，上限为 60
     */
    public Page<Anime> getAnimeList(Integer year, Season season, int pageNumber, int pageSize, SortBy sortBy) {
        int limitedPageSize = Math.min(pageSize, 60);

        LocalDateRange dateRange = seasonService.getStartDateRange(year, season);
        Specification<Anime> spec = Specification.allOf(
                AnimeSpec.startDateBetween(dateRange),
                AnimeSpec.reviewStatusEquals(ReviewStatus.APPROVED),
                AnimeSpec.orderBy(sortBy)
        );
        PageRequest pageRequest = PageRequest.of(pageNumber, limitedPageSize);
        return animeRepository.findAll(spec, pageRequest);
    }

    /**
     * 按多语种标题模糊搜索已审核动画。
     *
     * @param pageSize   每页数量，上限为 60
     * @return 动画分页结果；关键词为空时返回空页
     */
    public Page<Anime> getAnimeListBySearch(
            String keyword,
            Integer pageNumber,
            Integer pageSize
    ) {
        if (keyword == null || keyword.isBlank()) return Page.empty();

        int limitedPageSize = Math.min(pageSize, 60);

        Specification<Anime> spec = Specification.allOf(
                AnimeSpec.similarTitle(keyword),
                AnimeSpec.reviewStatusEquals(ReviewStatus.APPROVED)
        );
        PageRequest pageRequest = PageRequest.of(pageNumber, limitedPageSize);
        return animeRepository.findAll(spec, pageRequest);
    }

    /**
     * 按 animeId 查询已审核动画。
     *
     * @return Anime Entity；不存在或未审核通过时返回 {@code null}
     */
    public Anime getAnimeById(Long animeId) {
        return animeRepository.findByAnimeIdAndReviewStatus(animeId, ReviewStatus.APPROVED);
    }
}
