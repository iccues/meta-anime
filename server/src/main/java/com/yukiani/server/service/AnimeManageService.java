package com.yukiani.server.service;

import com.yukiani.server.entity.Anime;
import com.yukiani.server.entity.LocalDateRange;
import com.yukiani.server.entity.Mapping;
import com.yukiani.server.entity.ReviewStatus;
import com.yukiani.server.entity.Season;
import com.yukiani.server.exception.ResourceNotFoundException;
import com.yukiani.server.repo.AnimeRepository;
import com.yukiani.server.repo.MappingRepository;
import com.yukiani.server.spec.AnimeSpec;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 处理管理端 Anime CRUD 操作。
 */
@Service
public class AnimeManageService {

    @Resource
    AnimeRepository animeRepository;

    @Resource
    MappingRepository mappingRepository;

    @Resource
    SeasonService seasonService;

    @Resource
    private AnimeAggregationService animeAggregationService;

    /**
     * 按审核状态和开播季度查询动画。
     *
     * @param reviewStatus 审核状态；为空时不过滤
     * @param year         开播年份；为空时不过滤日期
     * @param season       开播季度；为空时查询全年
     */
    public List<Anime> getAnimeList(ReviewStatus reviewStatus, Integer year, Season season) {
        LocalDateRange dateRange = seasonService.getStartDateRange(year, season);
        Specification<Anime> spec = Specification.allOf(
                AnimeSpec.reviewStatusEquals(reviewStatus),
                AnimeSpec.startDateBetween(dateRange),
                AnimeSpec.orderByScoreNullLast()
        );
        return animeRepository.findAll(spec);
    }

    @Transactional
    public Anime createAnime(Anime anime) {
        return animeRepository.save(anime);
    }

    /**
     * 使用调用方提供的更新器修改指定动画。
     *
     * @throws ResourceNotFoundException 动画不存在时抛出
     */
    @Transactional
    public Anime updateAnime(Long animeId, Consumer<Anime> updater) {
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new ResourceNotFoundException("Anime", animeId));
        updater.accept(anime);
        return animeRepository.save(anime);
    }

    /**
     * 删除动画，并先解除其所有平台 Mapping。
     *
     * @throws ResourceNotFoundException 动画不存在时抛出
     */
    @Transactional
    public void deleteAnime(Long animeId) {
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new ResourceNotFoundException("Anime", animeId));

        // 使用副本遍历，避免解除双向关联时修改正在迭代的集合。
        List<Mapping> mappingsCopy = new ArrayList<>(anime.getMappings());
        mappingsCopy.forEach(mapping -> {
            animeAggregationService.removeMappingWithMetrics(anime, mapping);
            mappingRepository.save(mapping);
        });

        animeRepository.delete(anime);
    }

    /**
     * 删除所有未审核通过的动画，并清理失去关联的平台 Mapping。
     */
    @Transactional
    public void deleteNonApprovedAnimes() {
        animeRepository.deleteAllByReviewStatusIsNot(ReviewStatus.APPROVED);
        mappingRepository.deleteAllByAnimeIsNull();
    }
}
