package com.springcloud;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class WebSocketClient {

    private static String uri = "ws://192.168.1.179:19000/offline-shop?user=lf&password=lf";

    public static void main(String[] args) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            Session session = container.connectToServer(ClientEndpointConfig.Builder.create().configurator(
                    new ClientEndpointConfig.Configurator() {
//                        @Override
//                        public void beforeRequest(Map<String, List> headers) {
//
//                        }
                    }).build(), new URI(uri));
            session.getAsyncRemote().sendText("你好");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

