package com.yukiani.server.graphql;

import com.yukiani.server.generated.types.Season;
import com.yukiani.server.generated.types.SortBy;
import com.yukiani.server.generated.types.Anime;
import com.yukiani.server.generated.types.AnimePage;
import com.yukiani.server.service.AnimeQueryService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * 负责 Anime 查询并转换为 Schema Type。
 */
@Controller
public class AnimeQueryResolver {

    @Resource
    private AnimeQueryService animeQueryService;

    @Resource
    private GraphQLTypeMapper graphQLTypeMapper;

    /**
     * 分页查询指定年份和季度的已审核动画。
     *
     * @param year       开播年份；为空时不过滤日期
     * @param season     开播季度；年份存在时生效，为空则查询该年全年
     * @param pageNumber 从零开始的页码，默认值为 0
     * @param pageSize   每页数量，默认值为 30、最大值为 60
     * @param sortBy     排序指标，默认按综合评分排序
     * @return 非空的动画分页结果；无结果时 {@code content} 为空列表
     */
    @QueryMapping
    public AnimePage animeList(
            @Argument Integer year,
            @Argument Season season,
            @Argument Integer pageNumber,
            @Argument Integer pageSize,
            @Argument SortBy sortBy) {

        var animePage = animeQueryService.getAnimeList(
                year,
                graphQLTypeMapper.toSeason(season),
                pageNumber,
                pageSize,
                graphQLTypeMapper.toSortBy(sortBy)
        );

        return graphQLTypeMapper.toAnimePage(animePage);
    }

    /**
     * 按标题关键词分页搜索已审核动画。
     *
     * @param keyword    标题关键词；为空或空白字符串时返回空页
     * @param pageNumber 从零开始的页码，默认值为 0
     * @param pageSize   每页数量，默认值为 30、最大值为 60
     * @return 非空的动画分页搜索结果；无匹配项时 {@code content} 为空列表
     */
    @QueryMapping
    public AnimePage animeListBySearch(
            @Argument String keyword,
            @Argument Integer pageNumber,
            @Argument Integer pageSize
    ) {
        var animePage = animeQueryService.getAnimeListBySearch(
                keyword,
                pageNumber,
                pageSize
        );

        return graphQLTypeMapper.toAnimePage(animePage);
    }

    /**
     * 查询单个已审核动画。
     *
     * @param animeId 待查询动画的 animeId，不可为空
     * @return GraphQL 动画详情；不存在或未审核通过时返回 {@code null}
     */
    @QueryMapping
    public Anime anime(
            @Argument Long animeId
    ) {
        var anime = animeQueryService.getAnimeById(animeId);
        return graphQLTypeMapper.toGraphQLAnime(anime);
    }
}
