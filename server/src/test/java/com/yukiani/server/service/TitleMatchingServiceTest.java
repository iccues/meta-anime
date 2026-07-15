package com.yukiani.server.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TitleMatchingServiceTest {

    final TitleMatchingService titleMatchingService = new TitleMatchingService();

    @Test
    public void testAreTitlesSimilar_IdenticalTitles() {
        assertTrue(titleMatchingService.areTitlesSimilar(
                "Attack on Titan",
                "Attack on Titan"
        ));
    }

    @Test
    public void testAreTitlesSimilar_VerySimiiarTitles() {
        assertTrue(titleMatchingService.areTitlesSimilar(
                "Attack on Titan Season 1",
                "Attack on Titan Season 2"
        ));
    }

    @Test
    public void testAreTitlesSimilar_DifferentTitles() {
        assertFalse(titleMatchingService.areTitlesSimilar(
                "Attack on Titan",
                "Demon Slayer"
        ));
    }

    @Test
    public void testAreTitlesSimilar_WithNullTitle1() {
        assertFalse(titleMatchingService.areTitlesSimilar(
                null,
                "Attack on Titan"
        ));
    }

    @Test
    public void testAreTitlesSimilar_WithNullTitle2() {
        assertFalse(titleMatchingService.areTitlesSimilar(
                "Attack on Titan",
                null
        ));
    }

    @Test
    public void testAreTitlesSimilar_WithBothNull() {
        assertFalse(titleMatchingService.areTitlesSimilar(null, null));
    }

    @Test
    public void testAreTitlesSimilar_CaseDifference() {
        assertTrue(titleMatchingService.areTitlesSimilar(
                "Attack on Titan",
                "attack on titan"
        ));
    }

    @Test
    public void testAreTitlesSimilar_WithPunctuation() {
        assertTrue(titleMatchingService.areTitlesSimilar(
                "Re:Zero - Starting Life in Another World",
                "ReZero Starting Life in Another World"
        ));
    }

    @Test
    public void testAreTitlesSimilar_SimilarButNotIdentical() {
        // 测试相似但低于阈值的标题
        assertFalse(titleMatchingService.areTitlesSimilar(
                "Naruto",
                "Naruto Shippuden"
        ));
    }

    @Test
    public void testAreTitlesSimilar_EmptyStrings() {
        assertTrue(titleMatchingService.areTitlesSimilar("", ""));
    }

    @Test
    public void testAreTitlesSimilar_OneEmptyString() {
        assertFalse(titleMatchingService.areTitlesSimilar(
                "Attack on Titan",
                ""
        ));
    }

    @Test
    public void testAreTitlesSimilar_WithWhitespace() {
        assertTrue(titleMatchingService.areTitlesSimilar(
                "Attack on Titan",
                "Attack on  Titan"
        ));
    }

    @Test
    public void testAreTitlesSimilar_JapaneseTitles() {
        assertTrue(titleMatchingService.areTitlesSimilar(
                "進撃の巨人",
                "進撃の巨人"
        ));
    }

    @Test
    public void testAreTitlesSimilar_DifferentJapaneseTitles() {
        assertFalse(titleMatchingService.areTitlesSimilar(
                "進撃の巨人",
                "鬼滅の刃"
        ));
    }

    @Test
    public void testAreTitlesSimilar_WithUnicodeCompatibilityCharacters() {
        assertTrue(titleMatchingService.areTitlesSimilar(
                "ＳＰＹ×ＦＡＭＩＬＹ",
                "spy family"
        ));
    }

    @Test
    public void testAreTitlesSimilar_WithEquivalentUnicodeCharacters() {
        assertTrue(titleMatchingService.areTitlesSimilar(
                "Pokémon",
                "Poke\u0301mon"
        ));
    }

    @Test
    public void testAreTitlesSimilar_MixedLanguageTitles() {
        assertFalse(titleMatchingService.areTitlesSimilar(
                "Attack on Titan",
                "進撃の巨人"
        ));
    }

    @Test
    public void testAreTitlesSimilar_WithNumbers() {
        assertTrue(titleMatchingService.areTitlesSimilar(
                "Sword Art Online Season 1",
                "Sword Art Online Season 1"
        ));
    }

    @Test
    public void testAreTitlesSimilar_MinorTypo() {
        // 测试轻微的拼写错误 - 如果接近阈值应该仍然相似
        boolean result = titleMatchingService.areTitlesSimilar(
                "Attack on Titan",
                "Attack on Titen"
        );
        // 使用 0.95 阈值,这可能通过或失败,取决于准确分数
        assertTrue(result);
    }

    @Test
    public void testAreTitlesSimilar_MajorDifference() {
        assertFalse(titleMatchingService.areTitlesSimilar(
                "One Piece",
                "Two Pieces"
        ));
    }
}
