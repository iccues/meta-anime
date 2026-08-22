import { ref, watch } from "vue";
import { useRoute, useRouter, type RouteLocationRaw } from "vue-router";

import type { GetAnimeListBySearchQueryVariables } from "@/graphql/generated/graphql";

export function useSearchQuery() {
  const router = useRouter();
  const route = useRoute();

  // 搜索输入
  const searchInput = ref((route.query.q as string) || "");

  // 查询参数
  const searchParams = ref<GetAnimeListBySearchQueryVariables>({
    keyword: searchInput.value,
    pageNumber: route.query.page ? parseInt(route.query.page as string, 10) : 0,
    pageSize: 30,
  });

  // 路由变化时同步查询参数
  watch(
    () => route.query,
    (newQuery) => {
      searchInput.value = (newQuery.q as string) || "";
      searchParams.value = {
        keyword: searchInput.value,
        pageNumber: newQuery.page ? parseInt(newQuery.page as string, 10) : 0,
        pageSize: 30,
      };
    },
    { deep: true },
  );

  // 处理搜索提交
  const handleSearch = () => {
    const keyword = searchInput.value.trim();
    // 空关键词会清除 URL 中的查询参数。
    router.push({ query: keyword ? { q: keyword, page: 0 } : {} });
  };

  // 根据 0-base 页码生成分页链接
  const createPageLink = (pageNumber: number): RouteLocationRaw => {
    return {
      query: {
        ...route.query,
        page: pageNumber,
      },
    };
  };

  return {
    searchInput,
    searchParams,
    handleSearch,
    createPageLink,
  };
}
