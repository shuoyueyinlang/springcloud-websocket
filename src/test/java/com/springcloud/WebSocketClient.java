package com.springcloud;

import org.apache.tomcat.websocket.WsWebSocketContainer;

import javax.net.ssl.*;
import javax.websocket.*;
import javax.websocket.ClientEndpointConfig.Configurator;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebSocketClient implements Runnable {


    protected WsWebSocketContainer container;
    protected Session userSession = null;


    public WebSocketClient() {
        container = new WsWebSocketContainer();
    }


    public void connectServer(String sServer) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        try {
            final String encodedAuthToken = Base64.getEncoder().encodeToString("lf:lf".getBytes());
            List<String> values = new ArrayList<>(1);
            values.add("Basic " + encodedAuthToken);

            Configurator clientEndConfigurator = new Configurator() {
                @Override
                public void beforeRequest(Map<String, List<String>> headers) {
                    headers.put("Authorization", values);
                }
            };

            ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create().configurator(clientEndConfigurator).build();
            clientEndpointConfig.getUserProperties().put("org.apache.tomcat.websocket.WS_AUTHENTICATION_USER_NAME", "lf");
            clientEndpointConfig.getUserProperties().put("org.apache.tomcat.websocket.WS_AUTHENTICATION_PASSWORD", "lf");
            userSession = container.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session sess, EndpointConfig config) {
                    sess.addMessageHandler(new MessageHandler.Whole<String>() {
                        @Override
                        public void onMessage(String message) {
                            System.out.println("Receive From Server: " + message);
                        }
                    });
                    System.out.println("connected.");
                }
            }, clientEndpointConfig, new URI(sServer));

        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private SSLContext getSelfSignSSLContext() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        sc.init(new javax.net.ssl.X509KeyManager[]{},
                new TrustManager[]{},
                new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
        return sc;
    }


    public void sendMessage(String sMsg) throws IOException {
        userSession.getBasicRemote().sendText(sMsg);
    }


    public void disconnect() throws IOException {
        if (userSession == null) {
            System.out.println("userSession is a null object!!");
            return;
        }
        userSession.close();
    }

    @Override
    public void run() {
        connectUsingJavaWebSocket();
    }

    private void connectUsingJavaWebSocket() {
        WebSocketClient client = new WebSocketClient();
        String uri = "ws://192.168.1.179:19000/offline-shop";
        try {
            client.connectServer(uri);
            client.sendMessage("Hello. This message is from java client!!!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.submit(new WebSocketClient());
        System.out.println("Thread submitted to fixed pool.");
    }
//
//    private static String uri = "ws://192.168.1.179:19000/offline-shop?user=lf&password=lf";
//
//    public static void main(String[] args) {
//        try {
//            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//            Session session = container.connectToServer(Client.class, new URI(uri)); // 连接会话
//
//            char lf = 10; // 这个是换行
//            char nl = 0; // 这个是消息结尾的标记，一定要
//            StringBuilder sb = new StringBuilder();
//            sb.append("SEND").append(lf); // 请求的命令策略
//            sb.append("destination:/app/hello").append(lf); // 请求的资源
//            sb.append("content-length:14").append(lf).append(lf); // 消息体的长度
//            sb.append("WWW-Authenticate:").append(lf); // 消息体
//            sb.append("{\"name\":\"123\"}").append(nl); // 消息体
//            session.getBasicRemote().sendText("123132132131"); // 发送文本消息
//            session.getBasicRemote().sendText("4564546");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @ClientEndpoint
//    public static class Client {
//
//        @OnOpen
//        public void onOpen(Session session) {
//            System.out.println("Connected to endpoint: " + session.getBasicRemote());
//        }
//
//        @OnMessage
//        public void onMessage(String message) {
//            System.out.println(message);
//        }
//
//        @OnError
//        public void onError(Throwable t) {
//            t.printStackTrace();
//        }
//    }
}

