<script setup lang="ts" generic="T extends string | number | undefined">
import type { SelectOption } from "@pjyk-web/shared/constants/ui-options.ts";
import type { RouteLocationRaw } from "vue-router";

import LinkButton from "./LinkButton.vue";

defineProps<{
  label: string;
  options: readonly SelectOption<T>[];
  value: T;
  optionLink: (value: T) => RouteLocationRaw;
  disabled?: boolean;
}>();
</script>

<template>
  <div class="flex max-w-full min-w-0 items-center gap-[6px]" role="group" :aria-label="label">
    <span class="shrink-0 text-[13px] font-semibold text-gray-600">{{ label }}</span>

    <div class="flex w-fit max-w-full flex-wrap gap-[2px] rounded-[10px] bg-gray-100 p-[3px]">
      <LinkButton
        v-for="option in options"
        :key="String(option.value)"
        :to="optionLink(option.value)"
        :active="value === option.value"
        :disabled="disabled"
        class="h-[28px] justify-center rounded-[7px] px-[9px] text-[14px]"
      >
        {{ option.label }}
      </LinkButton>
    </div>
  </div>
</template>
