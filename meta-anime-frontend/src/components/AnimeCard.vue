<script setup lang="ts">
import type { Anime } from "../types/anime.ts";

defineProps<{
  anime: Anime,
}>()
</script>

<template>
  <div class="anime-card">
    <div class="image-container">
      <img :src="`${anime.coverImage}`" :alt="anime.title.titleCn"/>
    </div>
    <div class="card-content">
      <h3 class="anime-title">{{ anime.title.titleCn || anime.title.titleNative }}</h3>
    </div>
  </div>
</template>

<style scoped>
.anime-card {
  width: 200px;
  background-color: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
}

.anime-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.image-container {
  width: 100%;
  aspect-ratio: 3 / 4;
  overflow: hidden;
  background-color: #f0f0f0;
}

.image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.card-content {
  padding: 12px 16px;
  flex: 1;
  display: flex;
  align-items: flex-start;
}

.anime-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>