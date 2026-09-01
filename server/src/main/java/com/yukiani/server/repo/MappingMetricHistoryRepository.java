package com.yukiani.server.repo;

import com.yukiani.server.entity.MappingMetricHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MappingMetricHistoryRepository extends JpaRepository<MappingMetricHistory, Long> {
}
