package com.iccues.metaanimebackend.service;

import com.iccues.metaanimebackend.entity.Anime;
import com.iccues.metaanimebackend.entity.Mapping;
import com.iccues.metaanimebackend.entity.ReviewStatus;
import com.iccues.metaanimebackend.repo.AnimeRepository;
import com.iccues.metaanimebackend.service.fetch.AbstractAnimeFetchService;
import com.iccues.metaanimebackend.service.fetch.FetchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * MappingSyncService 测试类
 */
@ExtendWith(MockitoExtension.class)
class MappingSyncServiceTest {

    @Mock
    private AnimeRepository animeRepository;

    @Mock
    private FetchService fetchService;

    @Mock
    private ScoreService scoreService;

    @Mock
    private AbstractAnimeFetchService malFetchService;

    @InjectMocks
    private MappingSyncService mappingSyncService;

    @BeforeEach
    void setUp() {
        // 重置 pendingMappings 列表
        List<Mapping> pendingMappings = Collections.synchronizedList(new ArrayList<>());
        ReflectionTestUtils.setField(mappingSyncService, "pendingMappings", pendingMappings);
    }

    /**
     * 测试收集映射 - 成功场景
     */
    @Test
    void testCollectMappingsForSync_Success() {
        // 准备测试数据
        Anime anime1 = createAnime(1L, LocalDate.now().minusDays(30), ReviewStatus.APPROVED);
        Mapping mapping1 = createMapping("mal", "12345", anime1);
        anime1.setMappings(List.of(mapping1));

        Anime anime2 = createAnime(2L, LocalDate.now().minusDays(60), ReviewStatus.APPROVED);
        Mapping mapping2 = createMapping("mal", "67890", anime2);
        anime2.setMappings(List.of(mapping2));

        when(animeRepository.findAllByReviewStatus(ReviewStatus.APPROVED))
                .thenReturn(List.of(anime1, anime2));

        // 执行测试
        mappingSyncService.collectMappingsForSync();

        // 验证结果
        @SuppressWarnings("unchecked")
        List<Mapping> pendingMappings = (List<Mapping>) ReflectionTestUtils.getField(mappingSyncService, "pendingMappings");
        assertNotNull(pendingMappings);
        assertEquals(2, pendingMappings.size());
        assertTrue(pendingMappings.contains(mapping1));
        assertTrue(pendingMappings.contains(mapping2));

        verify(animeRepository, times(1)).findAllByReviewStatus(ReviewStatus.APPROVED);
    }

    /**
     * 测试收集映射 - 过滤最近 90 天内的动漫
     */
    @Test
    void testCollectMappingsForSync_FilterByDate() {
        // 准备测试数据 - 包含不同日期的动漫
        Anime recentAnime = createAnime(1L, LocalDate.now().minusDays(30), ReviewStatus.APPROVED);
        Mapping recentMapping = createMapping("mal", "12345", recentAnime);
        recentAnime.setMappings(List.of(recentMapping));

        Anime oldAnime = createAnime(2L, LocalDate.now().minusDays(100), ReviewStatus.APPROVED); // 超过 90 天
        Mapping oldMapping = createMapping("mal", "67890", oldAnime);
        oldAnime.setMappings(List.of(oldMapping));

        Anime futureAnime = createAnime(3L, LocalDate.now().plusDays(10), ReviewStatus.APPROVED); // 未来开播
        Mapping futureMapping = createMapping("mal", "11111", futureAnime);
        futureAnime.setMappings(List.of(futureMapping));

        Anime boundaryAnime = createAnime(4L, LocalDate.now().minusDays(89), ReviewStatus.APPROVED); // 边界情况
        Mapping boundaryMapping = createMapping("mal", "22222", boundaryAnime);
        boundaryAnime.setMappings(List.of(boundaryMapping));

        when(animeRepository.findAllByReviewStatus(ReviewStatus.APPROVED))
                .thenReturn(List.of(recentAnime, oldAnime, futureAnime, boundaryAnime));

        // 执行测试
        mappingSyncService.collectMappingsForSync();

        // 验证结果 - 只有最近 90 天内开播的动漫被收集
        @SuppressWarnings("unchecked")
        List<Mapping> pendingMappings = (List<Mapping>) ReflectionTestUtils.getField(mappingSyncService, "pendingMappings");
        assertNotNull(pendingMappings);
        assertEquals(2, pendingMappings.size());
        assertTrue(pendingMappings.contains(recentMapping));
        assertTrue(pendingMappings.contains(boundaryMapping));
        assertFalse(pendingMappings.contains(oldMapping));
        assertFalse(pendingMappings.contains(futureMapping));
    }

    /**
     * 测试收集映射 - 跳过没有开播日期的动漫
     */
    @Test
    void testCollectMappingsForSync_SkipNoStartDate() {
        // 准备测试数据
        Anime animeWithDate = createAnime(1L, LocalDate.now().minusDays(30), ReviewStatus.APPROVED);
        Mapping mapping1 = createMapping("mal", "12345", animeWithDate);
        animeWithDate.setMappings(List.of(mapping1));

        Anime animeWithoutDate = createAnime(2L, null, ReviewStatus.APPROVED);
        Mapping mapping2 = createMapping("mal", "67890", animeWithoutDate);
        animeWithoutDate.setMappings(List.of(mapping2));

        when(animeRepository.findAllByReviewStatus(ReviewStatus.APPROVED))
                .thenReturn(List.of(animeWithDate, animeWithoutDate));

        // 执行测试
        mappingSyncService.collectMappingsForSync();

        // 验证结果 - 只收集有开播日期的动漫
        @SuppressWarnings("unchecked")
        List<Mapping> pendingMappings = (List<Mapping>) ReflectionTestUtils.getField(mappingSyncService, "pendingMappings");
        assertNotNull(pendingMappings);
        assertEquals(1, pendingMappings.size());
        assertEquals(mapping1, pendingMappings.get(0));
    }

    /**
     * 测试收集映射 - 清空前一天的失败记录
     */
    @Test
    void testCollectMappingsForSync_ClearPreviousPending() {
        // 模拟前一天有失败的 mappings
        Anime oldAnime = createAnime(1L, LocalDate.now().minusDays(30), ReviewStatus.APPROVED);
        Mapping oldMapping = createMapping("mal", "99999", oldAnime);

        @SuppressWarnings("unchecked")
        List<Mapping> pendingMappings = (List<Mapping>) ReflectionTestUtils.getField(mappingSyncService, "pendingMappings");
        pendingMappings.add(oldMapping);

        // 准备新一天的数据
        Anime newAnime = createAnime(2L, LocalDate.now().minusDays(30), ReviewStatus.APPROVED);
        Mapping newMapping = createMapping("mal", "12345", newAnime);
        newAnime.setMappings(List.of(newMapping));

        when(animeRepository.findAllByReviewStatus(ReviewStatus.APPROVED))
                .thenReturn(List.of(newAnime));

        // 执行测试
        mappingSyncService.collectMappingsForSync();

        // 验证结果 - 旧的记录被清空，只包含新的
        assertEquals(1, pendingMappings.size());
        assertEquals(newMapping, pendingMappings.get(0));
        assertFalse(pendingMappings.contains(oldMapping));
    }

    /**
     * 测试收集映射 - 处理多个 mappings
     */
    @Test
    void testCollectMappingsForSync_MultipleMapping() {
        // 准备测试数据 - 一个动漫有多个 mappings
        Anime anime = createAnime(1L, LocalDate.now().minusDays(30), ReviewStatus.APPROVED);
        Mapping mapping1 = createMapping("mal", "12345", anime);
        Mapping mapping2 = createMapping("bangumi", "67890", anime);
        anime.setMappings(List.of(mapping1, mapping2));

        when(animeRepository.findAllByReviewStatus(ReviewStatus.APPROVED))
                .thenReturn(List.of(anime));

        // 执行测试
        mappingSyncService.collectMappingsForSync();

        // 验证结果 - 所有 mappings 都被收集
        @SuppressWarnings("unchecked")
        List<Mapping> pendingMappings = (List<Mapping>) ReflectionTestUtils.getField(mappingSyncService, "pendingMappings");
        assertNotNull(pendingMappings);
        assertEquals(2, pendingMappings.size());
        assertTrue(pendingMappings.contains(mapping1));
        assertTrue(pendingMappings.contains(mapping2));
    }

    /**
     * 测试同步单个映射 - 成功场景
     */
    @Test
    void testSyncMapping_Success() throws Exception {
        // 准备测试数据
        Anime anime = createAnime(1L, LocalDate.now().minusDays(30), ReviewStatus.APPROVED);
        Mapping mapping = createMapping("mal", "12345", anime);

        @SuppressWarnings("unchecked")
        List<Mapping> pendingMappings = (List<Mapping>) ReflectionTestUtils.getField(mappingSyncService, "pendingMappings");
        pendingMappings.add(mapping);

        when(fetchService.getFetchServiceByName("mal")).thenReturn(malFetchService);
        when(malFetchService.fetchAndSaveMapping("12345")).thenReturn(mapping);

        // 执行测试
        mappingSyncService.syncMapping(mapping);

        // 验证结果
        verify(fetchService, times(1)).getFetchServiceByName("mal");
        verify(malFetchService, times(1)).fetchAndSaveMapping("12345");
        assertFalse(pendingMappings.contains(mapping)); // 成功后应该从队列中移除
    }

    /**
     * 测试同步单个映射 - 找不到对应的 FetchService
     */
    @Test
    void testSyncMapping_NoFetchService() {
        // 准备测试数据
        Anime anime = createAnime(1L, LocalDate.now().minusDays(30), ReviewStatus.APPROVED);
        Mapping mapping = createMapping("unknown", "12345", anime);

        @SuppressWarnings("unchecked")
        List<Mapping> pendingMappings = (List<Mapping>) ReflectionTestUtils.getField(mappingSyncService, "pendingMappings");
        pendingMappings.add(mapping);

        when(fetchService.getFetchServiceByName("unknown")).thenReturn(null);

        // 执行测试
        mappingSyncService.syncMapping(mapping);

        // 验证结果
        verify(fetchService, times(1)).getFetchServiceByName("unknown");
        verify(malFetchService, never()).fetchAndSaveMapping(anyString());
        assertTrue(pendingMappings.contains(mapping)); // 未找到服务，保留在队列中
    }

    /**
     * 测试同步单个映射 - 抛出异常
     */
    @Test
    void testSyncMapping_ThrowsException() throws Exception {
        // 准备测试数据
        Anime anime = createAnime(1L, LocalDate.now().minusDays(30), ReviewStatus.APPROVED);
        Mapping mapping = createMapping("mal", "12345", anime);

        @SuppressWarnings("unchecked")
        List<Mapping> pendingMappings = (List<Mapping>) ReflectionTestUtils.getField(mappingSyncService, "pendingMappings");
        pendingMappings.add(mapping);

        when(fetchService.getFetchServiceByName("mal")).thenReturn(malFetchService);
        when(malFetchService.fetchAndSaveMapping("12345")).thenThrow(new RuntimeException("API Error"));

        // 执行测试 - 不应该抛出异常
        assertDoesNotThrow(() -> mappingSyncService.syncMapping(mapping));

        // 验证结果
        verify(fetchService, times(1)).getFetchServiceByName("mal");
        verify(malFetchService, times(1)).fetchAndSaveMapping("12345");
        assertTrue(pendingMappings.contains(mapping)); // 异常时保留在队列中
    }

    /**
     * 测试处理待同步映射 - 成功场景
     */
    @Test
    void testProcessPendingMappings_Success() throws Exception {
        // 准备测试数据
        Anime anime1 = createAnime(1L, LocalDate.now().minusDays(30), ReviewStatus.APPROVED);
        Mapping mapping1 = createMapping("mal", "12345", anime1);

        Anime anime2 = createAnime(2L, LocalDate.now().minusDays(60), ReviewStatus.APPROVED);
        Mapping mapping2 = createMapping("mal", "67890", anime2);

        @SuppressWarnings("unchecked")
        List<Mapping> pendingMappings = (List<Mapping>) ReflectionTestUtils.getField(mappingSyncService, "pendingMappings");
        pendingMappings.add(mapping1);
        pendingMappings.add(mapping2);

        when(fetchService.getFetchServiceByName("mal")).thenReturn(malFetchService);
        when(malFetchService.fetchAndSaveMapping(anyString())).thenReturn(mapping1, mapping2);
        doNothing().when(scoreService).calculateAllAverageScore();

        // 执行测试
        mappingSyncService.processPendingMappings();

        // 验证结果
        verify(fetchService, times(2)).getFetchServiceByName("mal");
        verify(malFetchService, times(1)).fetchAndSaveMapping("12345");
        verify(malFetchService, times(1)).fetchAndSaveMapping("67890");
        verify(scoreService, times(1)).calculateAllAverageScore();
        assertEquals(0, pendingMappings.size()); // 全部处理完成
    }

    /**
     * 测试处理待同步映射 - 空队列
     */
    @Test
    void testProcessPendingMappings_EmptyQueue() {
        // 准备测试数据 - 空队列
        @SuppressWarnings("unchecked")
        List<Mapping> pendingMappings = (List<Mapping>) ReflectionTestUtils.getField(mappingSyncService, "pendingMappings");
        assertTrue(pendingMappings.isEmpty());

        doNothing().when(scoreService).calculateAllAverageScore();

        // 执行测试
        mappingSyncService.processPendingMappings();

        // 验证结果
        verify(fetchService, never()).getFetchServiceByName(anyString());
        verify(scoreService, times(1)).calculateAllAverageScore();
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建测试用 Anime 对象
     */
    private Anime createAnime(Long id, LocalDate startDate, ReviewStatus status) {
        Anime anime = new Anime();
        anime.setAnimeId(id);
        anime.setStartDate(startDate);
        anime.setReviewStatus(status);
        anime.setMappings(new ArrayList<>());
        return anime;
    }

    /**
     * 创建测试用 Mapping 对象
     */
    private Mapping createMapping(String platform, String platformId, Anime anime) {
        Mapping mapping = new Mapping();
        mapping.setSourcePlatform(platform);
        mapping.setPlatformId(platformId);
        mapping.setAnime(anime);
        return mapping;
    }
}
