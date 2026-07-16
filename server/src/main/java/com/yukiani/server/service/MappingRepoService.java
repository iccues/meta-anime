package com.yukiani.server.service;

import com.yukiani.server.entity.Anime;
import com.yukiani.server.entity.Mapping;
import com.yukiani.server.repo.MappingRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 封装平台 Mapping 的幂等保存及关联动画指标刷新逻辑。
 */
@Service
public class MappingRepoService {
    @Resource
    MappingRepository repo;

    @Resource
    MetricService metricService;

    /**
     * 按平台 natural key 新增或更新 Mapping；更新现有 Mapping 后同步刷新动画指标。
     */
    @Transactional
    public void saveOrUpdate(Mapping m) {
        var existing = repo.findBySourcePlatformAndPlatformId(m.getSourcePlatform(), m.getPlatformId());
        if (existing != null) {
            existing.setRawScore(m.getRawScore());
            existing.setNormalizedScore(m.getNormalizedScore());
            existing.setMappingInfo(m.getMappingInfo());
            existing.setUpdateTime(m.getUpdateTime());
            existing.setRawPopularity(m.getRawPopularity());
            existing.setNormalizedPopularity(m.getNormalizedPopularity());

            Anime anime = existing.getAnime();
            if (anime != null) {
                metricService.calculateMetric(anime);
            }

            repo.save(existing);
        } else {
            repo.save(m);
        }
    }
}
