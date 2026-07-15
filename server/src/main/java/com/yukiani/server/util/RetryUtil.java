package com.yukiani.server.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.function.Supplier;

/**
 * 为外部平台请求提供同步重试机制。
 *
 * <p>4xx 直接抛出，5xx 和其他临时异常按固定间隔重试。</p>
 */
@Slf4j
public class RetryUtil {

    /** 默认最大重试次数，不包含首次执行。 */
    public static final int DEFAULT_MAX_RETRIES = 3;

    /** 默认重试间隔，单位为毫秒。 */
    public static final long DEFAULT_RETRY_DELAY_MS = 1000;

    /**
     * 使用指定次数和间隔重试操作。
     *
     * @param maxRetries    最大重试次数，不含首次执行
     * @param retryDelayMs  重试间隔（毫秒）
     */
    public static <T> T executeWithRetry(Supplier<T> operation, int maxRetries, long retryDelayMs, String operationName) {
        int attempt = 0;
        Exception lastException = null;

        while (attempt <= maxRetries) {
            try {
                if (attempt > 0) {
                    log.warn("Retrying {} operation, attempt {}/{}", operationName, attempt, maxRetries);
                }
                return operation.get();
            } catch (WebClientResponseException e) {
                lastException = e;
                // 4xx 通常无法通过重试恢复，立即交由调用方处理。
                if (e.getStatusCode().is4xxClientError()) {
                    log.warn("{} operation failed with client error: {} {}",
                            operationName, e.getStatusCode(), e.getMessage());
                    throw e;
                }
                // 5xx 可能是暂时故障，在剩余次数内继续重试。
                if (attempt < maxRetries) {
                    log.warn("{} operation failed, will retry: {} {}",
                            operationName, e.getStatusCode(), e.getMessage());
                    sleep(retryDelayMs);
                    attempt++;
                } else {
                    log.error("{} operation failed after {} retries: {} {}",
                            operationName, maxRetries, e.getStatusCode(), e.getResponseBodyAsString());
                    throw e;
                }
            } catch (Exception e) {
                lastException = e;
                if (attempt < maxRetries) {
                    log.warn("{} operation failed, will retry: {}", operationName, e.getMessage());
                    sleep(retryDelayMs);
                    attempt++;
                } else {
                    log.error("{} operation failed after {} retries: {}", operationName, maxRetries, e.getMessage());
                    throw e;
                }
            }
        }

        // 循环中的成功或异常分支都会返回或抛出，此处仅满足编译器的返回路径要求。
        if (lastException != null) {
            throw new RuntimeException(lastException);
        }
        throw new RuntimeException("Unexpected error in retry logic");
    }

    /**
     * 使用默认次数和间隔重试操作。
     */
    public static <T> T executeWithRetry(Supplier<T> operation, String operationName) {
        return executeWithRetry(operation, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY_MS, operationName);
    }

    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Retry sleep interrupted");
        }
    }
}
