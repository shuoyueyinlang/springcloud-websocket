package com.springcloud.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

/**
 * socket通信配置
 *
 * @author 林锋
 * @email 904303298@qq.com
 * @create 2018-04-13 13:08
 **/
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebSocketSecurityConfig.class);

    /**
     * 注册长连接端口后缀
     *
     * @param stompEndpointRegistry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint("/offline-shop").withSockJS();
    }

    /**
     * 配置消息类型
     *
     * @param messageBrokerRegistry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        // 设置客户端订阅消息的基础路径
        messageBrokerRegistry.setApplicationDestinationPrefixes("/app");
        // 设置服务器广播消息的基础路径
        messageBrokerRegistry.enableSimpleBroker("/topic", "/queue");
    }

    /**
     * 配置约束
     *
     * @param messages
     */
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpDestMatchers("/queue/**", "/topic/**").hasAnyRole("USER");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
            @Override
            public WebSocketHandler decorate(final WebSocketHandler handler) {
                return new WebSocketHandlerDecorator(handler) {
                    @Override
                    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
                        String username = session.getPrincipal().getName();
                        LOGGER.debug("客户端[username:{},sessionId:{}, url:{}]上线", username, session.getId(), session.getRemoteAddress().getHostString());
                        super.afterConnectionEstablished(session);
                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
                            throws Exception {
                        String username = session.getPrincipal().getName();
                        LOGGER.debug("客户端[username:{},sessionId:{}, url:{}]离线", username, session.getId(), session.getRemoteAddress().getHostString());
                        super.afterConnectionClosed(session, closeStatus);
                    }
                };
            }
        });
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
