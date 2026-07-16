package com.yukiani.server.entity;

/**
 * 动画主数据的人工审核状态。
 */
public enum ReviewStatus {
    PENDING,
    /** 审核通过，可在公开 API 中展示。 */
    APPROVED,
    REJECTED;
}
