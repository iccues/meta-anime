<script setup lang="ts">
import AnimeCard from "./AnimeCard.vue";
import type { Anime } from "../types/anime.ts";

defineProps<{
  animes: Anime[]
}>();
</script>

<template>
  <div class="grid grid-cols-[repeat(auto-fill,minmax(200px,1fr))] gap-5 justify-items-center">
    <AnimeCard v-for="(anime, index) in animes" :key="index" :anime="anime"/>
  </div>
</template>
