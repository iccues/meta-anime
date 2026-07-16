package com.yukiani.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 暴露给管理端的 OIDC Client 配置。
 */
@ConfigurationProperties(prefix = "oidc")
public record OidcConfig(
        String issuer,
        String clientId
) {}
