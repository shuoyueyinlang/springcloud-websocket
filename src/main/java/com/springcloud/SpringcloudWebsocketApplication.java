package com.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
public class SpringcloudWebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudWebsocketApplication.class, args);
    }
}
