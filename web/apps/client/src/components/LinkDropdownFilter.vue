<script setup lang="ts" generic="T extends string | number | undefined">
import type { SelectOption } from "@pjyk-web/shared/constants/ui-options.ts";
import { computed, onBeforeUnmount, onMounted, ref, useId } from "vue";
import type { RouteLocationRaw } from "vue-router";

import LinkButton from "./LinkButton.vue";

const props = defineProps<{
  label: string;
  options: readonly SelectOption<T>[];
  value: T;
  optionLink: (value: T) => RouteLocationRaw;
}>();

const dropdown = ref<HTMLElement>();
const isOpen = ref(false);
const panelId = useId();

// 值不在选项里时（例如手写 URL 传了超出范围的年份）显示原值，避免按钮空白
const currentLabel = computed(() => {
  const matched = props.options.find((option) => option.value === props.value);
  return matched?.label ?? String(props.value ?? props.label);
});

const closeDropdown = () => {
  isOpen.value = false;
};

const toggleDropdown = () => {
  isOpen.value = !isOpen.value;
};

const handlePointerDown = (event: PointerEvent) => {
  if (!dropdown.value?.contains(event.target as Node)) closeDropdown();
};

onMounted(() => {
  document.addEventListener("pointerdown", handlePointerDown);
});

onBeforeUnmount(() => {
  document.removeEventListener("pointerdown", handlePointerDown);
});
</script>

<template>
  <div class="flex items-center gap-[6px]" role="group" :aria-label="label">
    <span class="shrink-0 text-[13px] font-semibold text-gray-600">{{ label }}</span>

    <div ref="dropdown" class="relative">
      <button
        ref="trigger"
        type="button"
        class="inline-flex h-[30px] min-w-[104px] items-center justify-between gap-[8px] rounded-[8px] border border-gray-500 bg-white px-[10px] text-[14px] font-semibold text-gray-900 transition-colors hover:bg-gray-200 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
        :aria-label="`${label}：${currentLabel}`"
        :aria-expanded="isOpen"
        :aria-controls="isOpen ? panelId : undefined"
        @click="toggleDropdown"
      >
        <span>{{ currentLabel }}</span>
        <span
          class="size-[7px] border-r border-b border-current transition-transform"
          :class="isOpen ? 'translate-y-[2px] rotate-[225deg]' : '-translate-y-[2px] rotate-45'"
          aria-hidden="true"
        />
      </button>

      <Transition
        enter-active-class="transition duration-150 ease-out motion-reduce:transition-none"
        enter-from-class="-translate-y-[4px] opacity-0"
        leave-active-class="transition duration-100 ease-in motion-reduce:transition-none"
        leave-to-class="-translate-y-[4px] opacity-0"
      >
        <div
          v-if="isOpen"
          :id="panelId"
          class="dropdown-options absolute top-full left-0 z-50 mt-[6px] flex max-h-[320px] min-w-full flex-col gap-[2px] overflow-y-auto rounded-[10px] bg-white p-[3px] shadow-[0_2px_8px_rgba(0,0,0,0.14)]"
        >
          <LinkButton
            v-for="option in options"
            :key="String(option.value)"
            :to="optionLink(option.value)"
            :active="option.value === value"
            class="h-[30px] w-full shrink-0 rounded-[7px] px-[9px] text-[14px] whitespace-nowrap"
            @click="closeDropdown"
          >
            {{ option.label }}
          </LinkButton>
        </div>
      </Transition>
    </div>
  </div>
</template>

<style scoped>
.dropdown-options {
  scrollbar-width: none;
}

.dropdown-options::-webkit-scrollbar {
  display: none;
}
</style>
