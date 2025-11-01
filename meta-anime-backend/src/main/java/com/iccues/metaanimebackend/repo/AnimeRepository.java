package com.iccues.metaanimebackend.repo;

import com.iccues.metaanimebackend.entity.Anime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    List<Anime> findByStartDateBetween(LocalDate startDateAfter, LocalDate startDateBefore);
}
