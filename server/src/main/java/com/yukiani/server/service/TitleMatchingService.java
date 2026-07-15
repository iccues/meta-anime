package com.yukiani.server.service;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;

/**
 * 使用 Jaro-Winkler 相似度判断两个动画标题是否指向同一作品。
 */
@Service
public class TitleMatchingService {

    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

    /** 标题自动匹配所需的最低相似度。 */
    private static final double SIMILARITY_THRESHOLD = 0.95;

    /**
     * 检查两个动画标题是否可能指向同一作品。
     *
     * @return 完全一致或相似度达到阈值时返回 {@code true}
     */
    public boolean areTitlesSimilar(String title1, String title2) {
        if (title1 == null || title2 == null) {
            return false;
        }

        String normalized1 = normalize(title1);
        String normalized2 = normalize(title2);

        // 完全一致无需再执行相似度算法。
        if (normalized1.equals(normalized2)) {
            return true;
        }

        double score = similarity.apply(normalized1, normalized2);
        return score >= SIMILARITY_THRESHOLD;
    }

    /**
     * 标题归一化扩展点；当前保留原始字符和大小写。
     */
    private String normalize(String input) {
        return input;
    }
}
