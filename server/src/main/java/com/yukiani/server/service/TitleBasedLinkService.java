package com.yukiani.server.service;

import com.yukiani.server.entity.Anime;
import com.yukiani.server.entity.Mapping;
import com.yukiani.server.entity.MappingInfo;
import com.yukiani.server.repo.AnimeRepository;
import com.yukiani.server.repo.MappingRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 根据开播日期和标题相似度，将孤立平台 Mapping 关联到动画主数据。
 */
@Service
public class TitleBasedLinkService {
    @Resource
    protected AnimeRepoService animeRepoService;

    @Resource
    protected AnimeRepository animeRepository;
    @Resource
    protected MappingRepository mappingRepository;

    @Resource
    AnimeAggregationService animeAggregationService;

    /**
     * 查找标题和开播日期匹配的动画，未找到时创建新动画。
     */
    @Transactional
    Anime findOrCreateAnime(MappingInfo mappingInfo) {
        Anime existing = animeRepoService.findAnime(
                mappingInfo.getStartDate(),
                mappingInfo.getTitle()
        );

        if (existing != null) {
            return existing;
        }

        Anime anime = animeRepoService.createAnime(mappingInfo);
        return animeRepository.save(anime);
    }

    /**
     * 将尚未关联且具有开播日期的 Mapping 关联到已有或新建动画。
     */
    @Transactional
    public void linkMappingToAnime(Mapping mapping) {
        if (mapping.getAnime() == null && mapping.getMappingInfo().getStartDate() != null) {
            Anime anime = findOrCreateAnime(mapping.getMappingInfo());
            animeAggregationService.addMappingIfAbsent(anime, mapping);
            animeRepository.save(anime);
        }
    }

    @Transactional
    public void linkAllOrphanedMappings() {
        List<Mapping> mappings = mappingRepository.findAllByAnimeIsNullAndMappingInfo_StartDateIsNotNull();
        for (Mapping mapping : mappings) {
            linkMappingToAnime(mapping);
        }
    }
}
