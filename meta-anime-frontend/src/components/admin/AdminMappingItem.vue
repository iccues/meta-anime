<script setup lang="ts">
import type { AdminMapping } from '../../types/adminAnime';

defineProps<{
  mapping: AdminMapping;
}>();
</script>

<template>
  <div class="flex items-center py-1.5 px-2.5 gap-2.5 bg-white border border-gray-200 rounded-sm transition-all min-h-[50px] cursor-grab hover:bg-gray-50 hover:border-gray-300 hover:shadow-sm active:cursor-grabbing">
    <div class="shrink-0 w-20 flex flex-col items-start py-0.5">
      <span class="text-[10px] text-gray-500 leading-tight mb-0.5">映射ID</span>
      <span class="text-xs text-gray-900 font-medium whitespace-nowrap overflow-hidden text-ellipsis w-full">{{ mapping.mappingId }}</span>
    </div>
    <div class="shrink-0 w-[100px] flex justify-center py-0.5">
      <el-tag type="primary" size="small" effect="plain">{{ mapping.sourcePlatform }}</el-tag>
    </div>
    <div class="shrink-0 w-20 flex flex-col items-start py-0.5">
      <span class="text-[10px] text-gray-500 leading-tight mb-0.5">动画ID</span>
      <span class="text-xs text-gray-900 font-medium whitespace-nowrap overflow-hidden text-ellipsis w-full">{{ mapping.animeId }}</span>
    </div>
    <div class="flex-1 min-w-0 flex flex-col items-start py-0.5">
      <span class="text-[10px] text-gray-500 leading-tight mb-0.5">平台ID</span>
      <span class="text-[11px] text-gray-900 font-medium whitespace-nowrap overflow-hidden text-ellipsis w-full font-mono">{{ mapping.platformId }}</span>
    </div>
    <div class="shrink-0 w-[70px] flex flex-col items-start py-0.5">
      <span class="text-[10px] text-gray-500 leading-tight mb-0.5">评分</span>
      <span class="text-xs text-amber-500 font-semibold whitespace-nowrap overflow-hidden text-ellipsis w-full">{{ mapping.rawScore }}</span>
    </div>
  </div>
</template>

<style scoped>
@media (max-width: 768px) {
  .flex.items-center {
    flex-wrap: wrap;
  }

  .flex.flex-col {
    width: auto !important;
  }
}
</style>
