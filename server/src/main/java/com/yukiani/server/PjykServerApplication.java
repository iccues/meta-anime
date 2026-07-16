package com.yukiani.server;

import com.yukiani.server.config.OidcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 后端服务启动入口，同时启用异步任务、定时任务和配置属性绑定。
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(OidcConfig.class)
public class PjykServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PjykServerApplication.class, args);
    }

}
