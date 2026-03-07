<script setup lang="ts">
import type { AnimeListFragment } from "@/graphql/generated/graphql";
import AnimeCard from "./AnimeCard.vue";
import type { CombinedError } from "@urql/vue";

const props = defineProps<{
  animeList?: AnimeListFragment | null;
  fetching: boolean;
  error?: CombinedError;
}>();

const emit = defineEmits<{
  "page-change": [page: number];
}>();
</script>

<template>
  <div v-if="error" class="text-center py-10 text-base text-red-600">{{ error }}</div>
  <div v-else-if="fetching" class="text-center py-10 text-base text-gray-600">加载中...</div>
  <div
    v-else-if="animeList && animeList.content.length > 0"
    class="grid grid-cols-[repeat(auto-fill,12.5rem)] gap-5 justify-center"
  >
    <AnimeCard v-for="anime in animeList.content" :key="anime.animeId" :anime="anime" />
  </div>
  <div v-else class="text-center py-10 text-base text-gray-600">暂无数据</div>

  <div v-if="animeList?.pageInfo" class="flex flex-wrap justify-center items-center gap-2 mt-8">
    <el-pagination
      :current-page="(animeList.pageInfo.number || 0) + 1"
      :page-size="animeList.pageInfo.size"
      :total="animeList.pageInfo.totalElements"
      :page-count="animeList.pageInfo.totalPages"
      layout="prev, pager, next"
      @current-change="emit('page-change', $event - 1)"
    />
    <span class="text-[14px] text-gray-500">共 {{ animeList.pageInfo.totalElements }} 部</span>
  </div>
</template>
