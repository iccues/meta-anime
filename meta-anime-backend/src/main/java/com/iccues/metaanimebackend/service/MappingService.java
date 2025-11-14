package com.iccues.metaanimebackend.service;

import com.iccues.metaanimebackend.entity.Mapping;
import com.iccues.metaanimebackend.repo.MappingRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MappingService {
    @Resource
    MappingRepository repo;

    public void saveOrUpdate(Mapping m) {
        var existing = repo.findBySourcePlatformAndPlatformId(m.getSourcePlatform(), m.getPlatformId());
        if (existing != null) {
            existing.setRawScore(m.getRawScore());
            existing.setNormalizedScore(m.getNormalizedScore());
            existing.setRawJSON(m.getRawJSON());
            existing.setUpdateTime(m.getUpdateTime());
            repo.save(existing);
        } else {
            repo.save(m);
        }
    }
}
