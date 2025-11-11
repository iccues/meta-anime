<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getAnimeList, getUnmappedMappingList, updateMappingAnime, deleteAnime, createAnime, updateAnime } from '../../api/admin';
import type { AdminAnime, AdminMapping } from '../../types/adminAnime';
import AdminAnimeItem from '../../components/admin/AdminAnimeItem.vue';
import AdminMappingItem from '../../components/admin/AdminMappingItem.vue';
import AnimeFormDialog from '../../components/admin/AnimeFormDialog.vue';
import draggable from 'vuedraggable';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';

const animeList = ref<AdminAnime[]>([]);
const mappingList = ref<AdminMapping[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

onMounted(async () => {
  try {
    loading.value = true;
    const [animes, mappings] = await Promise.all([
      getAnimeList(),
      getUnmappedMappingList()
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

const applyMappingChange = async (mapping: AdminMapping, newAnimeId: number | null) => {
  const oldAnimeId = mapping.animeId
  if (oldAnimeId === newAnimeId) return

  try {
    await updateMappingAnime(mapping.mappingId, newAnimeId)

    if (oldAnimeId !== null) {
      const oldAnime = animeList.value.find(a => a.animeId === oldAnimeId)
      if (oldAnime) oldAnime.mappings = oldAnime.mappings.filter(m => m.mappingId !== mapping.mappingId)
    } else {
      const idx = mappingList.value.findIndex(m => m.mappingId === mapping.mappingId)
      if (idx !== -1) mappingList.value.splice(idx, 1)
    }

    mapping.animeId = newAnimeId

    if (newAnimeId !== null) {
      const newAnime = animeList.value.find(a => a.animeId === newAnimeId)
      if (newAnime && !newAnime.mappings.some(m => m.mappingId === mapping.mappingId)) {
        newAnime.mappings.push(mapping)
      }
      ElMessage.success('映射关联成功')
    } else {
      if (!mappingList.value.some(m => m.mappingId === mapping.mappingId)) {
        mappingList.value.push(mapping)
      }
      ElMessage.success('已解除映射关联')
    }
  } catch (e) {
    ElMessage.error('更新失败: ' + (e instanceof Error ? e.message : '未知错误'))
  }
}

const handleMappingToAnime = (evt: any, animeId: number) => {
  const mapping = evt.added?.element as AdminMapping | undefined
  if (mapping) applyMappingChange(mapping, animeId)
}

const handleMappingToUnmapped = (evt: any) => {
  const mapping = evt.added?.element as AdminMapping | undefined
  if (mapping) applyMappingChange(mapping, null)
}

const handleDeleteAnime = async (animeId: number) => {
  const anime = animeList.value.find(a => a.animeId === animeId)
  if (!anime) return

  try {
    const mappingCount = anime.mappings.length
    const confirmMessage = mappingCount > 0
      ? `确定要删除动画《${anime.title.titleCn || anime.title.titleNative}》吗？\n该动画包含 ${mappingCount} 个映射，将先解除关联后再删除动画。\n此操作不可恢复。`
      : `确定要删除动画《${anime.title.titleCn || anime.title.titleNative}》吗？此操作不可恢复。`

    await ElMessageBox.confirm(
      confirmMessage,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: false,
      }
    )

    // 保存映射列表用于更新前端状态
    const mappingsToMove = [...anime.mappings]

    // 调用后端删除 API（后端会自动解除映射关联）
    await deleteAnime(animeId)

    // 更新前端状态：将映射移到未关联列表
    mappingsToMove.forEach(mapping => {
      mapping.animeId = null
      if (!mappingList.value.some(m => m.mappingId === mapping.mappingId)) {
        mappingList.value.push(mapping)
      }
    })

    // 从列表中移除该动画
    const idx = animeList.value.findIndex(a => a.animeId === animeId)
    if (idx !== -1) {
      animeList.value.splice(idx, 1)
    }

    ElMessage.success('删除成功')
  } catch (e) {
    if (e === 'cancel') return
    ElMessage.error('删除失败: ' + (e instanceof Error ? e.message : '未知错误'))
  }
}

// 表单对话框相关
const dialogVisible = ref(false)
const editingAnime = ref<AdminAnime | undefined>(undefined)

// 打开创建动画对话框
const handleCreateAnime = () => {
  editingAnime.value = undefined
  dialogVisible.value = true
}

// 打开编辑动画对话框
const handleEditAnime = (anime: AdminAnime) => {
  editingAnime.value = anime
  dialogVisible.value = true
}

// 关闭对话框
const handleCloseDialog = () => {
  dialogVisible.value = false
  editingAnime.value = undefined
}

// 提交表单
const handleSubmitForm = async (formData: any) => {
  try {
    if (editingAnime.value) {
      // 编辑模式
      const updated = await updateAnime({
        animeId: editingAnime.value.animeId,
        ...formData
      })

      // 更新列表中的动画数据
      const idx = animeList.value.findIndex(a => a.animeId === editingAnime.value!.animeId)
      if (idx !== -1) {
        // 保留 mappings，只更新其他字段
        animeList.value[idx] = {
          ...updated,
          mappings: animeList.value[idx]?.mappings || []
        }
      }

      ElMessage.success('动画更新成功')
    } else {
      // 创建模式
      const created = await createAnime(formData)

      // 将新动画添加到列表顶部
      animeList.value.unshift(created)

      ElMessage.success('动画创建成功')
    }

    handleCloseDialog()
  } catch (e) {
    ElMessage.error('操作失败: ' + (e instanceof Error ? e.message : '未知错误'))
  }
}
</script>

<template>
  <div class="p-5 max-w-[1800px] mx-auto">
    <el-page-header title="返回" content="管理后台" class="mb-6" />

    <div v-loading="loading" class="min-h-[400px]">
      <el-alert
        v-if="error"
        :title="error"
        type="error"
        center
        show-icon
        :closable="false"
      />

      <el-row v-else :gutter="24">
        <!-- 左列：动画列表 -->
        <el-col :xs="24" :lg="12">
          <div class="flex flex-col">
            <div class="flex justify-between items-center mb-4 pb-3 border-b-2 border-gray-200">
              <div class="flex items-center gap-3">
                <h2 class="text-xl font-semibold text-gray-800 m-0">动画列表</h2>
                <el-tag type="info">{{ animeList.length }}</el-tag>
              </div>
              <el-button
                type="primary"
                size="small"
                :icon="Plus"
                @click="handleCreateAnime"
              >
                新建动画
              </el-button>
            </div>
            <el-scrollbar height="calc(100vh - 200px)">
              <div class="flex flex-col gap-3 pr-2">
                <AdminAnimeItem
                  v-for="anime in animeList"
                  :key="anime.animeId"
                  :anime="anime"
                  @mapping-change="(evt) => handleMappingToAnime(evt, anime.animeId)"
                  @delete-anime="handleDeleteAnime"
                  @edit-anime="handleEditAnime"
                />
              </div>
            </el-scrollbar>
          </div>
        </el-col>

        <!-- 右列：未关联映射列表 -->
        <el-col :xs="24" :lg="12">
          <div class="flex flex-col">
            <div class="flex justify-between items-center mb-4 pb-3 border-b-2 border-gray-200">
              <h2 class="text-xl font-semibold text-gray-800 m-0">未关联映射</h2>
              <el-tag type="warning">{{ mappingList.length }}</el-tag>
            </div>
            <el-scrollbar height="calc(100vh - 200px)">
              <div class="min-h-[200px] p-2 rounded transition-colors">
                <draggable
                  :model-value="mappingList"
                  :group="{ name: 'mappings', pull: true, put: true }"
                  item-key="mappingId"
                  :sort="false"
                  @change="handleMappingToUnmapped"
                  class="flex flex-col gap-3 pr-2"
                >
                  <template #item="{ element }">
                    <AdminMappingItem :mapping="element" />
                  </template>
                </draggable>
              </div>
            </el-scrollbar>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 动画表单对话框 -->
    <AnimeFormDialog
      :visible="dialogVisible"
      :anime="editingAnime"
      @close="handleCloseDialog"
      @submit="handleSubmitForm"
    />
  </div>
</template>

<style scoped>
@media (max-width: 992px) {
  .flex.flex-col > .flex.justify-between:first-child {
    margin-bottom: 1.5rem;
  }
}
</style>
