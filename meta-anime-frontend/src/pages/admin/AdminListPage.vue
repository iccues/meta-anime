<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getAnimeList, getMappingList } from '../../api/admin';
import type { AdminAnime, AdminMapping } from '../../types/adminAnime';
import AdminAnimeItem from '../../components/admin/AdminAnimeItem.vue';
import AdminMappingItem from '../../components/admin/AdminMappingItem.vue';

const animeList = ref<AdminAnime[]>([]);
const mappingList = ref<AdminMapping[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

onMounted(async () => {
  try {
    loading.value = true;
    const [animes, mappings] = await Promise.all([
      getAnimeList(),
      getMappingList()
    ]);
    animeList.value = animes;
    mappingList.value = mappings;
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载数据失败';
    console.error('Failed to load admin data:', e);
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="p-5 max-w-screen-xl mx-auto">
    <h1 class="text-3xl font-bold mb-6 text-gray-800">管理后台</h1>

    <div v-if="loading" class="text-center py-10 text-lg">
      加载中...
    </div>

    <div v-else-if="error" class="text-center py-10 text-lg text-red-600">
      {{ error }}
    </div>

    <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-6 items-start">
      <!-- 左列：动画列表 -->
      <div class="flex flex-col h-[calc(100vh-150px)]">
        <h2 class="text-xl font-semibold mb-4 pb-3 border-b-2 border-gray-200 text-gray-700">
          动画列表 ({{ animeList.length }})
        </h2>
        <div class="flex-1 overflow-y-auto pr-2 space-y-3 scrollbar-thin scrollbar-thumb-gray-400 scrollbar-track-gray-100 scrollbar-thumb-rounded">
          <AdminAnimeItem
            v-for="anime in animeList"
            :key="anime.animeId"
            :anime="anime"
          />
        </div>
      </div>

      <!-- 右列：映射列表 -->
      <div class="flex flex-col h-[calc(100vh-150px)]">
        <h2 class="text-xl font-semibold mb-4 pb-3 border-b-2 border-gray-200 text-gray-700">
          映射列表 ({{ mappingList.length }})
        </h2>
        <div class="flex-1 overflow-y-auto pr-2 space-y-3 scrollbar-thin scrollbar-thumb-gray-400 scrollbar-track-gray-100 scrollbar-thumb-rounded">
          <AdminMappingItem
            v-for="mapping in mappingList"
            :key="mapping.mappingId"
            :mapping="mapping"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 自定义滚动条样式 - Tailwind的scrollbar插件在v4可能不可用，使用原生CSS */
.scrollbar-thin::-webkit-scrollbar {
  width: 8px;
}

.scrollbar-track-gray-100::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.scrollbar-thumb-gray-400::-webkit-scrollbar-thumb {
  background: #9ca3af;
  border-radius: 4px;
}

.scrollbar-thumb-gray-400::-webkit-scrollbar-thumb:hover {
  background: #6b7280;
}

.scrollbar-thumb-rounded::-webkit-scrollbar-thumb {
  border-radius: 4px;
}

@media (max-width: 1024px) {
  .grid-cols-1.lg\:grid-cols-2 > div {
    height: 600px;
  }
}
</style>
