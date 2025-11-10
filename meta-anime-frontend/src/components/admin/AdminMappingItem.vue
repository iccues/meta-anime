<script setup lang="ts">
import type { AdminMapping } from '../../types/adminAnime';

defineProps<{
  mapping: AdminMapping;
}>();
</script>

<template>
  <div
    class="flex flex-col gap-3 bg-white border border-gray-200 rounded-lg p-4 transition-all duration-200 cursor-pointer hover:shadow-lg hover:-translate-y-0.5"
  >
    <div class="flex justify-between items-center pb-2 border-b border-gray-100">
      <span class="text-sm font-semibold text-gray-800">映射ID: {{ mapping.mappingId }}</span>
      <span class="bg-blue-600 text-white px-3 py-1 rounded-full text-xs font-medium">
        {{ mapping.sourcePlatform }}
      </span>
    </div>
    <div class="flex flex-col gap-1.5">
      <div class="flex justify-between text-sm">
        <span class="text-gray-600 font-medium">动画ID:</span>
        <span class="text-gray-800 font-mono">{{ mapping.animeId }}</span>
      </div>
      <div class="flex justify-between text-sm">
        <span class="text-gray-600 font-medium">平台ID:</span>
        <span class="text-gray-800 font-mono">{{ mapping.platformId }}</span>
      </div>
      <div class="flex justify-between text-sm">
        <span class="text-gray-600 font-medium">原始评分:</span>
        <span class="text-gray-800 font-mono">{{ mapping.rawScore }}</span>
      </div>
    </div>
  </div>
</template>
