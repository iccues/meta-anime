import adminClient from "./request";

/**
 * 抓取动画数据（完整流程：抓取映射、合并映射、计算评分）
 * @param year 年份
 * @param season 季度（SPRING, SUMMER, FALL, WINTER，可选）
 * @param platform 平台（Bangumi, AniList, MyAnimeList，可选）
 */
export async function fetchAnime(
  year: number,
  season?: string,
  platform?: string,
): Promise<string> {
  const response = await adminClient.post<string>("/api/admin/tasks/anime-fetch", null, {
    params: { year: year.toString(), season, platform },
  });
  return response.data;
}

/**
 * 抓取映射数据
 * @param year 年份
 * @param season 季度（SPRING, SUMMER, FALL, WINTER，可选）
 * @param platform 平台（Bangumi, AniList, MyAnimeList，可选）
 */
export async function fetchMapping(
  year: number,
  season?: string,
  platform?: string,
): Promise<string> {
  const response = await adminClient.post<string>("/api/admin/tasks/mapping-fetch", null, {
    params: { year: year.toString(), season, platform },
  });
  return response.data;
}

/**
 * 合并映射数据
 */
export async function linkMappings(): Promise<string> {
  const response = await adminClient.post<string>("/api/admin/tasks/mapping-link", null);
  return response.data;
}

/**
 * 重新计算 Mapping 归一化指标和 Anime 聚合指标
 */
export async function recalculateMetrics(): Promise<string> {
  const response = await adminClient.post<string>("/api/admin/tasks/metric-recalculation", null);
  return response.data;
}
