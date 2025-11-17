package com.iccues.metaanimebackend.controller;

import com.iccues.metaanimebackend.common.Response;
import com.iccues.metaanimebackend.dto.AnimeDTO;
import com.iccues.metaanimebackend.entity.Anime;
import com.iccues.metaanimebackend.entity.LocalDateRange;
import com.iccues.metaanimebackend.entity.ReviewStatus;
import com.iccues.metaanimebackend.entity.Season;
import com.iccues.metaanimebackend.mapper.AnimeMapper;
import com.iccues.metaanimebackend.repo.AnimeRepository;
import com.iccues.metaanimebackend.service.SeasonService;
import com.iccues.metaanimebackend.spec.AnimeSpec;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/anime")
class AnimeController {

    @Resource
    AnimeRepository animeRepository;

    @Resource
    AnimeMapper animeMapper;

    @Resource
    SeasonService seasonService;

    @ResponseBody
    @GetMapping("/get_list")
    public Response<List<AnimeDTO>> getAnimeList(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Season season) {


        LocalDateRange dateRange = seasonService.getStartDateRange(year, season);
        Specification<Anime> spec = Specification.allOf(
                AnimeSpec.startDateBetween(dateRange),
                AnimeSpec.reviewStatusEquals(ReviewStatus.APPROVED),
                AnimeSpec.orderByScoreNullLast()
        );
        List<Anime> animeList = animeRepository.findAll(spec);

        List<AnimeDTO> dtoList = animeMapper.toDtoList(animeList);
        return Response.ok(dtoList);
    }
}
