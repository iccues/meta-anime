package com.yukiani.server.entity;

/**
 * 动画季度及其对应的起始月份。
 */
public enum Season {
    WINTER,
    SPRING,
    SUMMER,
    FALL;

    /**
     * 获取季度的起始月份。
     *
     * @return 取值为 1、4、7 或 10 的月份
     */
    public int toMonth() {
        return switch (this) {
            case WINTER -> 1;
            case SPRING -> 4;
            case SUMMER -> 7;
            case FALL -> 10;
        };
    }

    public String toLowerName() {
        return this.name().toLowerCase();
    }
}
