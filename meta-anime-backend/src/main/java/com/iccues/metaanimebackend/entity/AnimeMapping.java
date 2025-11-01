package com.iccues.metaanimebackend.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.NaturalId;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
public class AnimeMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long mappingId;

    Long animeId;

    @NaturalId
    String sourcePlatform;

    @NaturalId
    String platformId;

    Double rawScore;
    Double normalizedScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    JsonNode rawJSON;

    Instant updateTime;

    public AnimeMapping(String sourcePlatform, String platformId, JsonNode rawJSON) {
        this.sourcePlatform = sourcePlatform;
        this.platformId = platformId;
        this.rawJSON = rawJSON;
        this.updateTime = Instant.now();
    }
}
