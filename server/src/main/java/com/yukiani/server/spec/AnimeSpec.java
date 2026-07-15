package com.yukiani.server.spec;

import com.yukiani.server.entity.Anime;
import com.yukiani.server.entity.LocalDateRange;
import com.yukiani.server.entity.ReviewStatus;
import com.yukiani.server.entity.SortBy;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * 构建动画列表筛选、排序和标题搜索所需的 JPA Specification。
 */
public class AnimeSpec {
    /** 审核状态为空时不生成筛选条件。 */
    public static Specification<Anime> reviewStatusEquals(ReviewStatus reviewStatus) {
        return (root, query, criteriaBuilder) -> {
            if (reviewStatus == null) return null;
            return criteriaBuilder.equal(root.get("reviewStatus"), reviewStatus);
        };
    }

    /** 日期范围为空时不生成筛选条件。 */
    public static Specification<Anime> startDateBetween(LocalDateRange range) {
        return (root, query, criteriaBuilder) -> {
            if (range == null) return null;
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), range.start()));
            predicates.add(criteriaBuilder.lessThan(root.get("startDate"), range.end()));
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static Specification<Anime> orderByScoreNullLast() {
        return (root, query, criteriaBuilder) -> {
            if (query == null) return null;
            query.orderBy(
                    criteriaBuilder.asc(criteriaBuilder.isNull(root.get("averageScore"))),
                    criteriaBuilder.desc(root.get("averageScore")));
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Anime> orderByPopularityNullLast() {
        return (root, query, criteriaBuilder) -> {
            if (query == null) return null;
            query.orderBy(
                    criteriaBuilder.asc(criteriaBuilder.isNull(root.get("popularity"))),
                    criteriaBuilder.desc(root.get("popularity")));
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Anime> orderBy(SortBy sortBy) {
        return switch (sortBy) {
            case SortBy.SCORE -> orderByScoreNullLast();
            case SortBy.POPULARITY -> orderByPopularityNullLast();
        };
    }

    public static Specification<Anime> orderById() {
        return (root, query, criteriaBuilder) -> {
            if (query == null) return null;
            query.orderBy(criteriaBuilder.asc(root.get("id")));
            return criteriaBuilder.conjunction();
        };
    }

    /**
     * 构建跨语种标题的模糊搜索条件，并按最高词相似度排序。
     * 关键词为空时不生成筛选条件。
     */
    public static Specification<Anime> similarTitle(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isBlank()) return null;

            Path<?> titlePath = root.get("title");
            float threshold = 0.0f;

            List<String> fields = List.of("titleNative", "titleRomaji", "titleEn", "titleCn");

            // WHERE 使用子串匹配补足短词与长词之间可能没有 trigram 交集的情况。
            String likePattern = "%" + keyword + "%";
            List<Predicate> predicates = new ArrayList<>();
            for (String field : fields) {
                Expression<Float> sim = criteriaBuilder.function(
                        "word_similarity", Float.class,
                        criteriaBuilder.literal(keyword), titlePath.get(field));
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.greaterThan(sim, threshold),
                        criteriaBuilder.like(titlePath.get(field), likePattern)
                ));
            }

            // Count Query 不能携带 ORDER BY；数据查询按 GREATEST(similarity(...)) DESC 排序。
            if (query != null && !Long.class.equals(query.getResultType())) {
                @SuppressWarnings("unchecked")
                Expression<Float>[] simExprs = fields.stream()
                        .map(field -> criteriaBuilder.function(
                                "word_similarity", Float.class,
                                criteriaBuilder.literal(keyword), titlePath.get(field)))
                        .toArray(Expression[]::new);
                Expression<Float> greatest = criteriaBuilder.function("GREATEST", Float.class, simExprs);
                query.orderBy(criteriaBuilder.desc(greatest));
            }

            return criteriaBuilder.or(predicates.toArray(Predicate[]::new));
        };
    }
}
