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
  <div class="admin-list-page">
    <el-page-header title="返回" content="管理后台" class="page-header" />

    <div v-loading="loading" class="content-wrapper">
      <el-alert
        v-if="error"
        :title="error"
        type="error"
        center
        show-icon
        :closable="false"
      />

      <el-row v-else :gutter="24" class="list-row">
        <!-- 左列：动画列表 -->
        <el-col :xs="24" :lg="12">
          <div class="list-section">
            <div class="section-header">
              <h2>动画列表</h2>
              <el-tag type="info">{{ animeList.length }}</el-tag>
            </div>
            <el-scrollbar height="calc(100vh - 200px)" class="list-scrollbar">
              <div class="list-content">
                <AdminAnimeItem
                  v-for="anime in animeList"
                  :key="anime.animeId"
                  :anime="anime"
                />
              </div>
            </el-scrollbar>
          </div>
        </el-col>

        <!-- 右列：映射列表 -->
        <el-col :xs="24" :lg="12">
          <div class="list-section">
            <div class="section-header">
              <h2>映射列表</h2>
              <el-tag type="warning">{{ mappingList.length }}</el-tag>
            </div>
            <el-scrollbar height="calc(100vh - 200px)" class="list-scrollbar">
              <div class="list-content">
                <AdminMappingItem
                  v-for="mapping in mappingList"
                  :key="mapping.mappingId"
                  :mapping="mapping"
                />
              </div>
            </el-scrollbar>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<style scoped>
.admin-list-page {
  padding: 20px;
  max-width: 1800px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.content-wrapper {
  min-height: 400px;
}

.list-row {
  margin-top: 0;
}

.list-section {
  display: flex;
  flex-direction: column;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid var(--el-border-color-lighter);
}

.section-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0;
}

.list-scrollbar {
  flex: 1;
}

.list-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-right: 8px;
}

@media (max-width: 992px) {
  .list-section {
    margin-bottom: 24px;
  }
}
</style>
