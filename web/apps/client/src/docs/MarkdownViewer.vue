<script setup lang="ts">
import { katex } from "@mdit/plugin-katex";
import MarkdownIt from "markdown-it";

import "github-markdown-css/github-markdown-light.css";

const props = defineProps<{
  raw: string;
}>();

const md = new MarkdownIt();
md.use(katex, { throwOnError: false, output: "mathml" });
const content = md.render(props.raw);
</script>

<template>
  <div class="markdown-body" v-html="content"></div>
</template>

<style scoped>
/* github-markdown-css 会覆盖工具类，因此在组件内定义容器样式。 */
.markdown-body {
  box-sizing: border-box;
  max-width: 720px;
  margin-inline: auto;
  padding-block-start: 2.5rem;
  padding-inline: var(--page-gutter);
}

/* 块级公式超出时横向滚动，而非撑破布局 */
.markdown-body :deep(.katex-block) {
  overflow-x: auto;
  overflow-y: hidden;
}
</style>
