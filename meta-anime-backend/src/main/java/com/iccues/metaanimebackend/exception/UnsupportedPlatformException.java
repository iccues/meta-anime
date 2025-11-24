package com.iccues.metaanimebackend.exception;

/**
 * 不支持的平台异常
 */
public class UnsupportedPlatformException extends BusinessException {

    public UnsupportedPlatformException(String platformName) {
        super("UNSUPPORTED_PLATFORM", String.format("不支持的平台: %s", platformName));
    }
}
