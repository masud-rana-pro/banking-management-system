package com.sbms.common.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final LiveUpdateWebSocketHandler liveUpdateWebSocketHandler;
    private final LiveUpdateHandshakeInterceptor liveUpdateHandshakeInterceptor;

    public WebSocketConfig(LiveUpdateWebSocketHandler liveUpdateWebSocketHandler,
                           LiveUpdateHandshakeInterceptor liveUpdateHandshakeInterceptor) {
        this.liveUpdateWebSocketHandler = liveUpdateWebSocketHandler;
        this.liveUpdateHandshakeInterceptor = liveUpdateHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(liveUpdateWebSocketHandler, "/ws/live")
                .addInterceptors(liveUpdateHandshakeInterceptor)
                .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*");
    }
}
