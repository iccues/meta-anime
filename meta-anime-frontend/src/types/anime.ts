export interface Anime {
    animeId: number,
    title: {
        titleNative: string,
        titleRomaji: string,
        titleEn: string,
        titleCn: string,
    },
    coverImage: string,
    averageScore: number,
    mappings: [{
        mappingId: number,
        sourcePlatform: string,
        rawScore: number,
    }]
}
