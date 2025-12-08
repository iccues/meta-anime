package com.iccues.metaanimebackend.job;

import com.iccues.metaanimebackend.entity.Mapping;
import com.iccues.metaanimebackend.repo.MappingRepository;
import com.iccues.metaanimebackend.service.fetch.AbstractAnimeFetchService;
import com.iccues.metaanimebackend.service.fetch.FetchService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RawJsonToMappingInfoJob {
    @Resource
    MappingRepository mappingRepository;

    @Resource
    FetchService fetchService;

    @PostConstruct
    @Transactional
    public void init() {
        List<Mapping> mappingList = mappingRepository.findAll();
        for (Mapping mapping : mappingList) {
            AbstractAnimeFetchService service = fetchService.getFetchService(mapping.getSourcePlatform());
            mapping.setMappingInfo(service.getMappingInfo(mapping));
            mappingRepository.save(mapping);
        }
    }
}
