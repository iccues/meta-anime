import { computed, ref, watch } from "vue";
import { useRoute, useRouter, type RouteLocationRaw } from "vue-router";

import type { GetAnimeListQueryVariables } from "@/graphql/generated/graphql";
import { filtersToQuery, queryToFilters } from "@/utils/queryUtils";

export function useAnimeListQuery() {
  const router = useRouter();
  const route = useRoute();

  // 初始化参数
  const animeListParams = ref<GetAnimeListQueryVariables>({
    ...queryToFilters(route.query),
    pageSize: 30,
  });

  // 监听路由变化，同步到组件状态
  watch(
    () => route.query,
    (newQuery) => {
      animeListParams.value = {
        ...queryToFilters(newQuery),
        pageSize: 30,
      };
    },
    { deep: true },
  );

  // 为筛选器提供 v-model 支持
  const filtersModel = computed({
    get: () => animeListParams.value,
    set: (newFilters) => {
      router.push({
        query: filtersToQuery(newFilters),
      });
    },
  });

  // 根据 0-base 页码生成分页链接
  const createPageLink = (pageNumber: number): RouteLocationRaw => {
    return {
      query: filtersToQuery({
        ...animeListParams.value,
        pageNumber,
      }),
    };
  };

  return {
    animeListParams,
    filtersModel,
    createPageLink,
  };
}
