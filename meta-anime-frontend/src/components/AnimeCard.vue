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
      <div class="score-badge" v-if="anime.averageScore">
        {{ anime.averageScore.toFixed(0) }}
      </div>
    </div>
    <h3 class="anime-title">{{ anime.title.titleCn || anime.title.titleNative }}</h3>
  </div>
</template>

<style scoped>
.anime-card {
  width: 200px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.image-container {
  width: 100%;
  aspect-ratio: 3 / 4;
  overflow: hidden;
  background-color: #f0f0f0;
  position: relative;
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.anime-card:hover .image-container {
  transform: scale(1.02);
}

.image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.anime-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0;
  padding: 0 4px;
  line-height: 1.4;
  height: calc(1.4em * 2);
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.score-badge {
  position: absolute;
  bottom: 8px;
  right: 8px;
  color: #ffffff;
  font-size: 16px;
  font-weight: 700;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.8);
}
</style>