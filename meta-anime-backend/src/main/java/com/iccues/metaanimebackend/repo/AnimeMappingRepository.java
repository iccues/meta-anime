package com.iccues.metaanimebackend.repo;

import com.iccues.metaanimebackend.entity.AnimeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimeMappingRepository extends JpaRepository<AnimeMapping, Long> {
    AnimeMapping findBySourcePlatformAndPlatformId(String sourcePlatform, String platformId);
}
