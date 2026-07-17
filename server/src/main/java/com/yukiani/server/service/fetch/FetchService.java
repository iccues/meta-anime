package com.yukiani.server.service.fetch;

import com.yukiani.server.entity.Platform;
import com.yukiani.server.entity.Season;
import com.yukiani.server.service.TitleBasedLinkService;
import com.yukiani.server.service.MetricService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 编排异步平台抓取、Mapping 关联和综合指标计算任务。
 */
@Service
@Slf4j
public class FetchService {

    @Resource
    BangumiFetchService bangumiFetchService;
    @Resource
    AniListFetchService aniListFetchService;
    @Resource
    MyAnimeListFetchService myAnimeListFetchService;

    @Resource
    TitleBasedLinkService titleBasedLinkService;

    /**
     * 异步抓取平台 Mapping，并在抓取完成后关联孤立 Mapping。
     *
     * @param season   开播季度；为空时抓取全年
     * @param platform 目标平台；为空时依次抓取全部平台
     */
    @Async
    public void fetchAnime(int year, Season season, Platform platform) {
        fetchMapping(year, season, platform);
        linkMappings();
    }

    /**
     * 异步抓取并保存指定平台或全部平台的季度 Mapping。
     *
     * @param season   开播季度；为空时抓取全年
     * @param platform 目标平台；为空时依次抓取全部平台
     */
    @Async
    public void fetchMapping(int year, Season season, Platform platform) {
        switch (platform) {
            case Bangumi -> safeFetchMappings(bangumiFetchService, Platform.Bangumi, year, season);
            case AniList -> safeFetchMappings(aniListFetchService, Platform.AniList, year, season);
            case MyAnimeList -> safeFetchMappings(myAnimeListFetchService, Platform.MyAnimeList, year, season);
            case null -> {
                safeFetchMappings(bangumiFetchService, Platform.Bangumi, year, season);
                safeFetchMappings(aniListFetchService, Platform.AniList, year, season);
                safeFetchMappings(myAnimeListFetchService, Platform.MyAnimeList, year, season);
            }
        }
    }

    private void safeFetchMappings(AbstractAnimeFetchService service, Platform platform, int year, Season season) {
        try {
            log.debug("{} fetch start for year={}, season={}", platform, year, season);
            service.fetchAndSaveMappings(year, season);
            log.debug("{} fetch completed for year={}, season={}", platform, year, season);
        } catch (Exception e) {
            log.error("{} fetch failed for year={}, season={}: {}", platform, year, season, e.getMessage());
        }
    }

    @Async
    public void linkMappings() {
        try {
            log.debug("link mappings start");
            titleBasedLinkService.linkAllOrphanedMappings();
            log.debug("link mappings completed");
        } catch (Exception e) {
            log.error("link mappings failed: {}", e.getMessage());
        }
    }

    public AbstractAnimeFetchService getFetchService(Platform platform) {
        return switch (platform) {
            case Bangumi -> bangumiFetchService;
            case AniList -> aniListFetchService;
            case MyAnimeList -> myAnimeListFetchService;
        };
    }
}
