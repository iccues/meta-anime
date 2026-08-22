import type { SelectOption } from "../constants/ui-options.ts";

/**
 * 生成年份选项（从指定年份到当前年份）
 * @param startYear 开始的年份
 * @returns 年份选项数组，包含"全部"选项和年份选项
 */
export function generateYearOptionsFrom(startYear: number): SelectOption<number | undefined>[] {
  const currentYear = new Date().getFullYear();
  const years: SelectOption<number | undefined>[] = [{ label: "全部", value: undefined }];

  for (let year = currentYear; year >= startYear; year--) {
    years.push({ label: `${year}年`, value: year });
  }

  return years;
}
