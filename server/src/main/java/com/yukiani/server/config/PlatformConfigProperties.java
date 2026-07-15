package com.yukiani.server.config;

import com.yukiani.server.entity.Platform;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 绑定 {@code application.yml} 中 {@code platform.*} 的平台指标配置。
 */
@Configuration
@ConfigurationProperties(prefix = "platform")
@Data
public class PlatformConfigProperties {
    private PlatformConfig bangumi = new PlatformConfig();
    private PlatformConfig aniList = new PlatformConfig();
    private PlatformConfig myAnimeList = new PlatformConfig();

    public PlatformConfig getConfig(Platform platform) {
        return switch (platform) {
            case Bangumi -> bangumi;
            case AniList -> aniList;
            case MyAnimeList -> myAnimeList;
        };
    }
}
