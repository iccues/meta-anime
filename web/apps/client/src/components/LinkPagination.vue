<script setup lang="ts">
import { ArrowLeft, ArrowRight } from "@element-plus/icons-vue";
import { computed } from "vue";
import type { RouteLocationRaw } from "vue-router";

import LinkButton from "./LinkButton.vue";

const props = defineProps<{
  /** 当前页和 pageLink 参数均从 0 开始 */
  currentPage: number;
  pageCount: number;
  total: number;
  pageLink: (pageNumber: number) => RouteLocationRaw;
}>();

const pageItems = computed<(number | null)[]>(() => {
  const { currentPage, pageCount } = props;
  const lastPage = pageCount - 1;

  if (pageCount <= 9) {
    return Array.from({ length: pageCount }, (_, index) => index);
  }

  if (currentPage <= 4) {
    return [0, 1, 2, 3, 4, 5, 6, null, lastPage];
  }

  if (currentPage >= pageCount - 5) {
    return [
      0,
      null,
      lastPage - 6,
      lastPage - 5,
      lastPage - 4,
      lastPage - 3,
      lastPage - 2,
      lastPage - 1,
      lastPage,
    ];
  }

  return [
    0,
    null,
    currentPage - 2,
    currentPage - 1,
    currentPage,
    currentPage + 1,
    currentPage + 2,
    null,
    lastPage,
  ];
});

// 页码和省略号共用同一套几何样式
const cellClass = "inline-flex size-[30px] items-center justify-center rounded-[8px] text-[14px]";
</script>

<template>
  <div class="flex flex-col items-center gap-[8px]">
    <nav v-if="pageCount > 1" class="flex items-center gap-[2px]" aria-label="分页导航">
      <LinkButton
        :to="pageLink(Math.max(currentPage - 1, 0))"
        :disabled="currentPage <= 0"
        :class="cellClass"
        aria-label="上一页"
      >
        <el-icon :size="18"><ArrowLeft /></el-icon>
      </LinkButton>

      <template v-for="(item, index) in pageItems" :key="item ?? `ellipsis-${index}`">
        <span v-if="item === null" :class="[cellClass, 'text-gray-400']" aria-hidden="true">
          …
        </span>
        <LinkButton
          v-else
          :to="pageLink(item)"
          :active="item === currentPage"
          :aria-label="`第 ${item + 1} 页`"
          :class="cellClass"
        >
          {{ item + 1 }}
        </LinkButton>
      </template>

      <LinkButton
        :to="pageLink(Math.min(currentPage + 1, pageCount - 1))"
        :disabled="currentPage >= pageCount - 1"
        :class="cellClass"
        aria-label="下一页"
      >
        <el-icon :size="18"><ArrowRight /></el-icon>
      </LinkButton>
    </nav>

    <span class="text-[14px] text-gray-500">共 {{ total }} 部</span>
  </div>
</template>
