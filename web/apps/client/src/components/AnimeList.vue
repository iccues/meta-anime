<script setup lang="ts">
import type { CombinedError } from "@urql/vue";
import type { RouteLocationRaw } from "vue-router";

import type { AnimeListFragment } from "@/graphql/generated/graphql";

import AnimeCard from "./AnimeCard.vue";
import AnimeCardSkeleton from "./AnimeCardSkeleton.vue";
import LinkPagination from "./LinkPagination.vue";

const props = defineProps<{
  animeList?: AnimeListFragment;
  fetching: boolean;
  error?: CombinedError;
  pageLink: (pageNumber: number) => RouteLocationRaw;
}>();

const skeletonCount = 6;

const gridClass =
  "grid grid-cols-[repeat(auto-fill,minmax(var(--card-width),1fr))] gap-x-5 gap-y-7";
</script>

<template>
  <div v-if="error" class="py-10 text-center text-base text-red-600">{{ error }}</div>
  <div v-else-if="fetching">
    <div :class="gridClass">
      <AnimeCardSkeleton v-for="index in skeletonCount" :key="index" />
    </div>
  </div>
  <div v-else-if="animeList && animeList.content.length > 0">
    <div :class="gridClass">
      <AnimeCard v-for="anime in animeList.content" :key="anime.animeId" :anime="anime" />
    </div>

    <LinkPagination
      class="mt-8"
      :current-page="animeList.pageInfo.number"
      :page-count="animeList.pageInfo.totalPages"
      :total="animeList.pageInfo.totalElements"
      :page-link="pageLink"
    />
  </div>
  <div v-else class="py-10 text-center text-base text-gray-600">暂无数据</div>
</template>
