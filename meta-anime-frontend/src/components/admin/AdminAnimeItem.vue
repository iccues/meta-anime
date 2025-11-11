<script setup lang="ts">
import { ref } from 'vue';
import { ArrowDown, Delete, Edit } from '@element-plus/icons-vue';
import type { AdminAnime } from '../../types/adminAnime';
import AdminMappingItem from './AdminMappingItem.vue';
import draggable from 'vuedraggable';

const props = defineProps<{
  anime: AdminAnime;
}>();

const emit = defineEmits<{
  mappingChange: [evt: any];
  deleteAnime: [animeId: number];
  editAnime: [anime: AdminAnime];
}>();

const isExpanded = ref(false);

const toggleExpand = () => {
  isExpanded.value = !isExpanded.value;
};

const handleMappingChange = (evt: any) => {
  emit('mappingChange', evt);
};

const handleDelete = (event: Event) => {
  event.stopPropagation();
  emit('deleteAnime', props.anime.animeId);
};

const handleEdit = (event: Event) => {
  event.stopPropagation();
  emit('editAnime', props.anime);
};
</script>

<template>
  <div class="border border-gray-200 rounded bg-white transition-all" :class="{ 'border-blue-300': isExpanded, 'hover:shadow-md hover:border-gray-300': true }">
    <div class="flex items-center py-2 px-3 cursor-pointer gap-3 min-h-[60px] hover:bg-gray-50" @click="toggleExpand">
      <div class="shrink-0 w-[50px]">
        <el-image
          :src="anime.coverImage"
          :alt="anime.title.titleCn || anime.title.titleNative"
          fit="cover"
          class="w-[50px] h-[70px] rounded-sm"
        />
      </div>
      <div class="shrink-0 w-20 flex flex-col items-start py-1">
        <span class="text-[11px] text-gray-500 leading-tight mb-0.5">ID</span>
        <span class="text-[13px] text-gray-900 font-medium">{{ anime.animeId }}</span>
      </div>
      <div class="flex-1 min-w-0 flex flex-col items-start gap-0.5 py-1">
        <div class="text-sm font-semibold text-gray-900 whitespace-nowrap overflow-hidden text-ellipsis w-full">
          {{ anime.title.titleCn || anime.title.titleNative }}
        </div>
        <div class="text-xs text-gray-500 whitespace-nowrap overflow-hidden text-ellipsis w-full">
          {{ anime.title.titleNative }}
        </div>
      </div>
      <div class="shrink-0 w-20 flex flex-col items-start py-1">
        <span class="text-[11px] text-gray-500 leading-tight mb-0.5">评分</span>
        <span class="text-[13px] text-green-600 font-semibold">{{ anime.averageScore?.toFixed(3) }}</span>
      </div>
      <div class="shrink-0 w-[120px] flex justify-center items-center gap-2">
        <el-button
          type="primary"
          size="small"
          :icon="Edit"
          circle
          @click="handleEdit"
          title="编辑动画"
        />
        <el-button
          type="danger"
          size="small"
          :icon="Delete"
          circle
          @click="handleDelete"
          title="删除动画"
        />
        <el-icon class="text-sm transition-transform duration-300" :class="isExpanded ? 'rotate-180 text-blue-500' : 'text-gray-500'">
          <ArrowDown />
        </el-icon>
      </div>
    </div>

    <el-collapse-transition>
      <div v-show="isExpanded" class="border-t border-gray-200 bg-gray-50 py-2 px-3 pb-3" @click.stop>
        <draggable
          :model-value="anime.mappings"
          :group="{ name: 'mappings', pull: true, put: true }"
          item-key="mappingId"
          class="flex flex-col gap-1.5 min-h-[60px]"
          @change="handleMappingChange"
        >
          <template #item="{ element }">
            <AdminMappingItem :mapping="element" />
          </template>
        </draggable>
      </div>
    </el-collapse-transition>
  </div>
</template>

<style scoped>
@media (max-width: 768px) {
  .flex.items-center.py-2 {
    flex-wrap: wrap;
  }

  .flex-1.min-w-0 {
    width: 100%;
    order: -1;
    margin-bottom: 0.5rem;
  }
}
</style>
