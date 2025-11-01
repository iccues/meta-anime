package com.iccues.metaanimebackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.iccues.metaanimebackend.entity.Anime;
import com.iccues.metaanimebackend.entity.AnimeMapping;
import com.iccues.metaanimebackend.entity.AnimeTitles;
import com.iccues.metaanimebackend.repo.AnimeRepository;
import jakarta.annotation.Resource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BangumiFetchService {

    @Resource
    AnimeMappingService animeMappingService;

    final WebClient client = WebClient.create("https://api.bgm.tv/calendar");

    @Resource
    ObjectMapper mapper;

    @Resource
    private AnimeService animeService;

    @Resource
    AnimeRepository repo;

    Anime searchOrCreateAnime(JsonNode jsonNode) {
        String date = jsonNode.path("air_date").asText();
        LocalDate startDate = LocalDate.parse(date);

        AnimeTitles titles = new AnimeTitles();
        titles.setTitleNative(jsonNode.path("name").asText());
        titles.setTitleCn(jsonNode.path("name_cn").asText());

        Anime anime = animeService.findAnime(startDate, titles);

        anime.setCoverImage(jsonNode.path("images").path("large").asText());

        repo.save(anime);

        return anime;
    }

    AnimeMapping handleAnimeMapping(JsonNode jsonNode) {
        String platformId = jsonNode.path("id").asText();

        AnimeMapping mapping = new AnimeMapping("Bangumi", platformId, jsonNode);


        // score
        double rawScore = jsonNode.path("rating").path("score").asDouble();
        double normalizedScore = (rawScore - 1) / 9 * 100;
        mapping.setRawScore(rawScore);
        if (normalizedScore > 0) {
            mapping.setNormalizedScore(normalizedScore);
        }

        Anime anime = searchOrCreateAnime(jsonNode);

        mapping.setAnimeId(anime.getAnimeId());

        return mapping;
    }

    public void fetchAnime() {
        List<JsonNode> mediaList = fetchAnimeData();
        for (JsonNode jsonNode : mediaList) {
            AnimeMapping mapping = handleAnimeMapping(jsonNode);
            animeMappingService.saveOrUpdate(mapping);
        }
    }

    List<JsonNode> fetchAnimeData() {
        var weekdays = client.get()
                .retrieve()
                .bodyToMono(ArrayNode.class)
                .block();

        if (weekdays == null) {
            return new ArrayList<>();
        }

        List<JsonNode> jsonNodes = new ArrayList<>();

        for (JsonNode weekday : weekdays) {
            for (JsonNode item : weekday.path("items")) {
                jsonNodes.add(item);
            }

        }

        return jsonNodes;
    }
}
