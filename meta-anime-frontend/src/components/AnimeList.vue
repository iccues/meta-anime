<script setup lang="ts">
import AnimeCard from "./AnimeCard.vue";
import type { Anime } from "../types/anime.ts";

defineProps<{
  animes: Anime[]
}>();
</script>

<template>
  <div class="anime-list">
    <AnimeCard v-for="(anime, index) in animes" :key="index" :anime="anime"/>
  </div>
</template>

<style scoped>
.anime-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  justify-items: center;
}
</style>
