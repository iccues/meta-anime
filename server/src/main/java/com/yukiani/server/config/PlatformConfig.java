package com.yukiani.server.config;

import lombok.Data;

/**
 * 单个外部平台参与评分和热度计算的归一化参数。
 */
@Data
public class PlatformConfig {
    /** 归一化热度计入动画综合热度时使用的权重。 */
    private double popularityWeight = 1;

    /** 热度归一化基准；平台原始热度等于该值时映射为 10000。 */
    private double popularityMedian = 10000;

    /** 归一化评分参与动画加权平均时使用的权重。 */
    private double scoreWeight = 1;

    /**
     * 评分平滑使用的归一化热度先验强度；未配置时不平滑。
     */
    private Double scorePriorStrength;

    /** 评分 z-score 标准化使用的均值。 */
    private double scoreMean = 7.0;

    /** 评分 z-score 标准化使用的标准差。 */
    private double scoreStd = 1.0;
}
