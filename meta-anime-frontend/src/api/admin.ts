import { get } from './http';
import type { AdminAnime, AdminMapping } from '../types/adminAnime';

/**
 * 获取所有动画列表（管理后台）
 */
export async function getAnimeList(): Promise<AdminAnime[]> {
    return get<AdminAnime[]>('/api/admin/get_anime_list');
}

/**
 * 获取所有映射列表（管理后台）
 */
export async function getMappingList(): Promise<AdminMapping[]> {
    return get<AdminMapping[]>('/api/admin/get_mapping_list');
}
