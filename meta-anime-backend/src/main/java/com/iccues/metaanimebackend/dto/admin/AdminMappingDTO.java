package com.iccues.metaanimebackend.dto.admin;

import com.fasterxml.jackson.databind.JsonNode;

public record AdminMappingDTO(
        Long mappingId,
        Long animeId,
        String sourcePlatform,
        String platformId,
        Double rawScore,
        JsonNode rawJSON
) {
}
