package com.iccues.metaanimebackend.dto.admin;

import com.iccues.metaanimebackend.entity.AnimeTitles;

import java.time.LocalDate;
import java.util.List;

public record AdminAnimeDTO(
        Long animeId,
        AnimeTitles title,
        String coverImage,
        LocalDate startDate,
        Double averageScore,
        List<AdminMappingDTO> mappings
) {
}
