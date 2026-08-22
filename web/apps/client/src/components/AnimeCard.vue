<script setup lang="ts">
import { computed } from "vue";
import { RouterLink } from "vue-router";

import type { AnimeCardFragment } from "@/graphql/generated/graphql";

const props = defineProps<{
  anime: AnimeCardFragment;
}>();

const title = computed(() => props.anime.title.titleCn || props.anime.title.titleNative || "");
</script>

<template>
  <RouterLink :to="`/anime/${anime.animeId}`" class="group block no-underline">
    <div class="flex w-full flex-col gap-2">
      <div
        class="relative aspect-[1/1.4] w-full overflow-hidden rounded-2xl bg-gray-100 ring-1 ring-gray-900/10 transition duration-200 group-hover:-translate-y-2 group-hover:shadow-xl group-hover:shadow-gray-900/20"
      >
        <img
          v-if="anime.coverImage"
          class="block h-full w-full object-cover"
          :src="anime.coverImage"
          :alt="title || 'Anime Cover'"
          loading="lazy"
        />
        <div
          v-else
          class="flex h-full w-full flex-col items-center justify-center bg-gray-200 text-gray-400"
        >
          <span class="text-[14px] font-medium">暂无封面</span>
        </div>

        <div
          v-if="anime.averageScore"
          class="absolute inset-x-0 bottom-0 h-12 bg-gradient-to-t from-black/70 to-transparent"
        ></div>
        <div
          v-if="anime.averageScore"
          class="absolute right-3 bottom-1 text-[16px] font-bold text-white"
        >
          {{ anime.averageScore.toFixed(0) }}
        </div>
      </div>

      <h3
        class="m-0 line-clamp-2 h-[40px] px-2 text-[14px] leading-[1.4] font-medium text-gray-800"
        :title="title"
      >
        {{ title }}
      </h3>
    </div>
  </RouterLink>
</template>
