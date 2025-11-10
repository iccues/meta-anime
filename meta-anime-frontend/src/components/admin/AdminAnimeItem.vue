<script setup lang="ts">
import { ref } from 'vue';
import type { AdminAnime } from '../../types/adminAnime';
import AdminMappingItem from './AdminMappingItem.vue';

defineProps<{
  anime: AdminAnime;
}>();

const isExpanded = ref(false);

const toggleExpand = () => {
  isExpanded.value = !isExpanded.value;
};
</script>

<template>
  <div class="bg-white border border-gray-200 rounded-lg overflow-hidden transition-shadow hover:shadow-lg">
    <div
      class="flex gap-4 p-4 cursor-pointer transition-colors hover:bg-gray-50 relative"
      @click="toggleExpand"
    >
      <img
        :src="anime.coverImage"
        :alt="anime.title.titleCn || anime.title.titleRomaji"
        class="w-20 h-28 object-cover rounded flex-shrink-0"
      />
      <div class="flex-1 flex flex-col gap-2">
        <div class="text-base font-semibold text-gray-800 leading-snug">
          {{ anime.title.titleCn || anime.title.titleRomaji }}
        </div>
        <div class="text-sm text-gray-600 leading-tight">
          {{ anime.title.titleRomaji }}
        </div>
        <div class="flex gap-3 text-xs text-gray-500 mt-auto">
          <span class="bg-gray-100 px-2 py-1 rounded">ID: {{ anime.animeId }}</span>
          <span class="bg-gray-100 px-2 py-1 rounded">评分: {{ anime.averageScore }}</span>
          <span class="bg-gray-100 px-2 py-1 rounded">映射数: {{ anime.mappings.length }}</span>
        </div>
      </div>
      <div
        class="flex items-center justify-center w-6 h-6 text-gray-600 text-xs flex-shrink-0 my-auto transition-transform duration-300"
        :class="{ 'rotate-180': isExpanded }"
      >
        ▼
      </div>
    </div>

    <div
      v-if="isExpanded"
      class="border-t border-gray-200 p-4 bg-gray-50 animate-[slideDown_0.3s_ease-out]"
    >
      <div class="text-sm font-semibold text-gray-700 mb-3 pl-1">
        关联的映射 ({{ anime.mappings.length }})
      </div>
      <div class="flex flex-col gap-2">
        <AdminMappingItem
          v-for="mapping in anime.mappings"
          :key="mapping.mappingId"
          :mapping="mapping"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes slideDown {
  from {
    opacity: 0;
    max-height: 0;
  }
  to {
    opacity: 1;
    max-height: 1000px;
  }
}
</style>
