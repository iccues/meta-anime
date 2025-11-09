package com.iccues.metaanimebackend.dto;

public record MappingDTO(
        Long mappingId,
        String sourcePlatform,
        String platformId,
        Double rawScore
) {
}
