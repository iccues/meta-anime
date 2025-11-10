package com.iccues.metaanimebackend.dto;

import com.iccues.metaanimebackend.entity.AnimeTitles;

import java.time.LocalDate;
import java.util.List;

public record AnimeDTO(
        Long animeId,
        AnimeTitles title,
        String coverImage,
        LocalDate startDate,
        Double averageScore,
        List<MappingDTO> mappings
) {
}
