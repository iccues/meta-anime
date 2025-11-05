<script setup lang="ts">
import { ref, onMounted } from 'vue';
import AnimeList from "../components/AnimeList.vue";
import type { Anime } from "../types/anime.ts";
import { get } from '../api/http.ts';

const animes = ref<Anime[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

const fetchAnimes = async () => {
  try {
    loading.value = true;
    error.value = null;
    animes.value = await get<Anime[]>('/api/anime/get_list');
  } catch (err) {
    error.value = err instanceof Error ? err.message : '未知错误';
    console.error('获取动漫列表失败:', err);
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  fetchAnimes();
});
</script>

<template>
  <div class="p-5 max-w-[1400px] mx-auto">
    <h1 class="mb-6 text-gray-800">动漫列表</h1>
    <div v-if="loading" class="text-center py-10 text-base text-gray-600">加载中...</div>
    <div v-else-if="error" class="text-center py-10 text-base text-red-600">{{ error }}</div>
    <AnimeList v-else :animes="animes"/>
  </div>
</template>