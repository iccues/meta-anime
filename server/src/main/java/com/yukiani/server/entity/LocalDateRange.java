package com.yukiani.server.entity;

import java.time.LocalDate;

/**
 * 左闭右开的本地日期范围。
 *
 * @param start 起始日期（包含）
 * @param end   结束日期（不包含）
 */
public record LocalDateRange(LocalDate start, LocalDate end) {
}
