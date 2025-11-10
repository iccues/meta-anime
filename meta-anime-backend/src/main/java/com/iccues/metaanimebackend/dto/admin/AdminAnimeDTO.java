package com.iccues.metaanimebackend.dto.admin;

import com.iccues.metaanimebackend.dto.AnimeTitlesDTO;

import java.time.LocalDate;
import java.util.List;

public record AdminAnimeDTO(
        Long animeId,
        AnimeTitlesDTO title,
        String coverImage,
        LocalDate startDate,
        Double averageScore,
        List<AdminMappingDTO> mappings
) {
}
