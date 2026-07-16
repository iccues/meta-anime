package com.yukiani.server.service;

import com.yukiani.server.entity.*;
import org.springframework.stereotype.Service;

/**
 * 从平台 Mapping 聚合动画的标题、封面和开播日期。
 */
@Service
public class InfoService {
    /**
     * 重新聚合动画基础信息，并优先使用 MyAnimeList 封面。
     */
    public void aggregateInfo(Anime anime) {
        cleanInfo(anime);
        for (Mapping mapping : anime.getMappings()) {
            applyMappingInfo(anime, mapping);
        }
        setCoverImageFromMyAnimeList(anime);
    }

    /**
     * 使用单个平台的信息补齐动画当前为空的字段。
     */
    public void applyMappingInfo(Anime anime, Mapping mapping) {
        MappingInfo mappingInfo = mapping.getMappingInfo();
        if (mappingInfo == null) {
            return;
        }

        if (anime.getCoverImage() == null) {
            anime.setCoverImage(mappingInfo.getCoverImage());
        }
        if (anime.getStartDate() == null) {
            anime.setStartDate(mappingInfo.getStartDate());
        }

        if (anime.getTitle() == null) {
            anime.setTitle(new AnimeTitles());
        }
        if (mappingInfo.getTitle() != null) {
            anime.getTitle().merge(mappingInfo.getTitle());
        }
    }

    /**
     * 清空由平台 Mapping 聚合产生的动画基础信息。
     */
    public void cleanInfo(Anime anime) {
        anime.setCoverImage(null);
        anime.setStartDate(null);
        anime.setTitle(new AnimeTitles());
    }

    /**
     * 若存在 MyAnimeList Mapping 且封面有效，则使用其封面覆盖聚合结果。
     */
    public void setCoverImageFromMyAnimeList(Anime anime) {
        Mapping myAnimeList = anime.getMappingByPlatform(Platform.MyAnimeList);
        if (myAnimeList != null && myAnimeList.getMappingInfo() != null) {
            String coverImage = myAnimeList.getMappingInfo().getCoverImage();
            if (coverImage != null && !coverImage.isBlank()) {
                anime.setCoverImage(coverImage);
            }
        }
    }
}
