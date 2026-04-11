package com.cardioguard.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置
 * 用于实时推送心率监测数据
 */
@Slf4j
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(healthMonitorHandler(), "/ws/health-monitor")
                .setAllowedOrigins("*");
        log.info("WebSocket健康监控端点注册成功: /ws/health-monitor");
    }
    
    @Bean
    public HealthMonitorWebSocketHandler healthMonitorHandler() {
        return new HealthMonitorWebSocketHandler();
    }
}
