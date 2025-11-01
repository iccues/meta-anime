package com.iccues.metaanimebackend.controller;

import com.iccues.metaanimebackend.common.Response;
import com.iccues.metaanimebackend.entity.Anime;
import com.iccues.metaanimebackend.repo.AnimeRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/anime")
class AnimeController {

    @Resource
    AnimeRepository animeRepository;

    @ResponseBody
    @GetMapping("/get_list")
    public Response<List<Anime>> getAnimeList() {
        List<Anime> animeList = animeRepository.findAll();
        return Response.ok(animeList);
    }
}
