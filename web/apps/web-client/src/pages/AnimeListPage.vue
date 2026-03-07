<script setup lang="ts">
import { ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import AnimeFilter from "@/components/AnimeFilter.vue";
import AnimeList from "@/components/AnimeList.vue";
import { filtersToQuery, queryToFilters } from "@/utils/queryUtils";
import { useAnimeListHead } from "@/composables/useAnimeListHead";
import { useQuery } from "@urql/vue";
import { GetAnimeListDocument, type GetAnimeListQueryVariables } from "@/graphql/generated/graphql";

const router = useRouter();
const route = useRoute();

// 单一数据源：所有列表参数
const animeListParams = ref<GetAnimeListQueryVariables>({
  ...queryToFilters(route.query),
  pageSize: 30,
});

const { data, fetching, error } = useQuery({
  query: GetAnimeListDocument,
  variables: animeListParams,
});

// 监听参数变化，同步更新 URL Query
watch(animeListParams, () => router.push({ query: filtersToQuery(animeListParams.value) }), {
  deep: true,
  immediate: true,
});

// 动态 SEO 配置
useAnimeListHead(animeListParams);
</script>

<template>
  <div class="p-5 max-w-[1400px] mx-auto">
    <!-- 筛选器 -->
    <AnimeFilter v-model="animeListParams" />

    <AnimeList
      :animeList="data?.animeList"
      :fetching="fetching"
      :error="error"
      v-on:page-change="animeListParams.pageNumber = $event"
    />
  </div>
</template>
