package com.iccues.metaanimebackend.exception;

/**
 * 从外部平台获取数据失败异常
 */
public class FetchFailedException extends BusinessException {

    public FetchFailedException(String platform, String platformId) {
        super("FETCH_FAILED", "无法从 " + platform + " 获取数据: " + platformId);
    }
}
