<script setup lang="ts">
import { computed } from "vue";
import { RouterLink, type RouteLocationRaw } from "vue-router";

const props = defineProps<{
  to: RouteLocationRaw;
  active?: boolean;
  disabled?: boolean;
}>();

// 只有可跳转时渲染 RouterLink，选中和禁用状态渲染 span
const isLink = computed(() => !props.disabled && !props.active);

// 组件只负责状态样式，尺寸、圆角和字号由调用方通过 class 提供
// 禁用时保留选中态的底色和边框，只调淡配色，否则整组禁用后看不出当前选中项
const stateClass = computed(() => {
  if (props.active) {
    return props.disabled
      ? "cursor-not-allowed border-gray-300 bg-white font-semibold text-gray-400"
      : "border-gray-500 bg-white font-semibold text-gray-900";
  }

  return props.disabled
    ? "cursor-not-allowed border-transparent text-gray-300"
    : "border-transparent text-gray-600 hover:bg-gray-200";
});

const baseClass =
  "inline-flex items-center border transition-colors focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-500";
</script>

<template>
  <component
    :is="isLink ? RouterLink : 'span'"
    :to="isLink ? to : undefined"
    :aria-current="active ? 'true' : undefined"
    :aria-disabled="disabled || undefined"
    :class="[baseClass, stateClass]"
  >
    <slot />
  </component>
</template>
