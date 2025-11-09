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
            m.setMappingId(existing.getMappingId());
            repo.save(m);
        } else {
            repo.save(m);
        }
    }
}
