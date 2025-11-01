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
  <div class="main-page">
    <h1>动漫列表</h1>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <AnimeList v-else :animes="animes"/>
  </div>
</template>

<style scoped>
.main-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

h1 {
  margin-bottom: 24px;
  color: #333;
}

.loading, .error {
  text-align: center;
  padding: 40px;
  font-size: 16px;
}

.loading {
  color: #666;
}

.error {
  color: #e74c3c;
}
</style>