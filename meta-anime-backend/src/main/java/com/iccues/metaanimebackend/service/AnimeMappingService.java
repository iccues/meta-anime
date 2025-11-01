package com.iccues.metaanimebackend.service;

import com.iccues.metaanimebackend.entity.AnimeMapping;
import com.iccues.metaanimebackend.repo.AnimeMappingRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AnimeMappingService {
    @Resource
    AnimeMappingRepository repo;

    public void saveOrUpdate(AnimeMapping m) {
        var existing = repo.findBySourcePlatformAndPlatformId(m.getSourcePlatform(), m.getPlatformId());
        if (existing != null) {
            m.setMappingId(existing.getMappingId());
            repo.save(m);
        } else {
            repo.save(m);
        }
    }
}
