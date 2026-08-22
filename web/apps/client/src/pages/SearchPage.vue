<script setup lang="ts">
import { useQuery } from "@urql/vue";

import AnimeList from "@/components/AnimeList.vue";
import SearchBox from "@/components/SearchBox.vue";
import { useSearchHead } from "@/composables/useSearchHead";
import { useSearchQuery } from "@/composables/useSearchQuery";
import { GetAnimeListBySearchDocument } from "@/graphql/generated/graphql";

const { searchInput, searchParams, handleSearch, createPageLink } = useSearchQuery();

useSearchHead(searchParams);

const { data, fetching, error } = useQuery({
  query: GetAnimeListBySearchDocument,
  variables: searchParams,
  // 空关键词时暂停查询。
  pause: () => !searchParams.value.keyword,
});
</script>

<template>
  <div class="container-page pt-8">
    <SearchBox v-model="searchInput" class="mx-auto mb-8" @submit="handleSearch" />

    <!-- 空关键词时显示搜索提示。 -->
    <p v-if="!searchParams.keyword" class="py-10 text-center text-base text-gray-600">
      输入关键词开始搜索
    </p>

    <AnimeList
      v-else
      :animeList="data?.animeListBySearch"
      :fetching="fetching"
      :error="error"
      :page-link="createPageLink"
    />
  </div>
</template>
