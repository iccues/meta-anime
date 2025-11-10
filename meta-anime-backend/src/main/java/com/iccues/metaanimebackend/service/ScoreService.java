package com.iccues.metaanimebackend.service;

import com.iccues.metaanimebackend.entity.Anime;
import com.iccues.metaanimebackend.entity.Mapping;
import com.iccues.metaanimebackend.repo.AnimeRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreService {

    @Resource
    AnimeRepository animeRepository;

    @Transactional
    public void calculateAllAverageScore() {
        List<Anime> list = animeRepository.findAll();

        for (Anime anime : list) {
            calculateAverageScore(anime);
        }
    }

    @Transactional
    public void calculateAverageScore(Anime anime) {
        double totalScore = 0.0;
        int i = 0;

        for (Mapping mapping : anime.getMappings()) {
            Double normalizedScore = mapping.getNormalizedScore();
            if (normalizedScore != null && normalizedScore > 0) {
                totalScore += normalizedScore;
                i++;
            }
        }

        if (i > 0) {
            anime.setAverageScore(totalScore / i);
        } else  {
            anime.setAverageScore(null);
        }
    }
}
