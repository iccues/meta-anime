package com.iccues.metaanimebackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Anime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long animeId;

    @Embedded
    AnimeTitles title;

    String coverImage;

    LocalDate startDate;

    @OneToMany(mappedBy = "anime",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    List<AnimeMapping> mappings = new ArrayList<>();

    public void addMapping(AnimeMapping mapping) {
        this.mappings.add(mapping);
        mapping.setAnime(this);
    }

    public void removeMapping(AnimeMapping mapping) {
        this.mappings.remove(mapping);
        mapping.setAnime(null);
    }
}
