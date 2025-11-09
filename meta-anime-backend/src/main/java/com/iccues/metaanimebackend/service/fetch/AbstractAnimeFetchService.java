package com.iccues.metaanimebackend.service.fetch;

import com.fasterxml.jackson.databind.JsonNode;
import com.iccues.metaanimebackend.entity.Anime;
import com.iccues.metaanimebackend.entity.Mapping;
import com.iccues.metaanimebackend.entity.AnimeTitles;
import com.iccues.metaanimebackend.entity.Season;
import com.iccues.metaanimebackend.repo.AnimeRepository;
import com.iccues.metaanimebackend.service.MappingService;
import com.iccues.metaanimebackend.service.AnimeService;
import jakarta.annotation.Resource;

import java.time.LocalDate;
import java.util.List;

public abstract class AbstractAnimeFetchService {
    @Resource
    protected MappingService mappingService;

    @Resource
    protected AnimeService animeService;

    @Resource
    protected AnimeRepository repo;

    protected abstract String getPlatform();

    protected abstract LocalDate extractStartDate(JsonNode jsonNode);

    protected abstract AnimeTitles extractTitles(JsonNode jsonNode);

    protected abstract String extractCoverImage(JsonNode jsonNode);

    protected abstract String extractPlatformId(JsonNode jsonNode);

    protected abstract double extractRawScore(JsonNode jsonNode);

    protected abstract double normalizeScore(double rawScore);

    Anime searchOrCreateAnime(JsonNode jsonNode) {
        LocalDate startDate = extractStartDate(jsonNode);
        AnimeTitles titles = extractTitles(jsonNode);

        Anime anime = animeService.findAnime(startDate, titles);

        anime.setCoverImage(extractCoverImage(jsonNode));

        return repo.save(anime);
    }

    void handleMapping(JsonNode jsonNode) {
        String platformId = extractPlatformId(jsonNode);

        Mapping mapping = new Mapping(getPlatform(), platformId, jsonNode);

        double rawScore = extractRawScore(jsonNode);
        if (rawScore > 0) {
            mapping.setRawScore(rawScore);
            double normalizedScore = normalizeScore(rawScore);
            if (normalizedScore > 0) {
                mapping.setNormalizedScore(normalizedScore);
            }
        }

        mappingService.saveOrUpdate(mapping);

        if (mapping.getAnime() == null) {
            Anime anime = searchOrCreateAnime(jsonNode);
            anime.addMapping(mapping);
            repo.save(anime);
        }
    }

    public void fetchAnime(int year, Season season) {
        List<JsonNode> mediaList = fetchAnimeData(year, season);
        for (JsonNode jsonNode : mediaList) {
            handleMapping(jsonNode);
        }
    }

    protected abstract List<JsonNode> fetchAnimeData(int year, Season season);
}
