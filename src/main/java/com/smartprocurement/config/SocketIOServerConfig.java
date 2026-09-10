package com.smartprocurement.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PreDestroy;

@Configuration
public class SocketIOServerConfig {

    @Value("${socketio.host:localhost}")
    private String host;

    @Value("${socketio.port:8085}")
    private Integer port;

    private SocketIOServer server;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setOrigin("*"); // Allow CORS

        server = new SocketIOServer(config);

        server.addConnectListener(client -> {
            System.out.println("Socket.IO client connected: " + client.getSessionId());
        });

        server.addDisconnectListener(client -> {
            System.out.println("Socket.IO client disconnected: " + client.getSessionId());
        });

        server.addEventListener("join_room", String.class, (client, roomName, ackSender) -> {
            client.joinRoom(roomName);
            System.out.println("Client " + client.getSessionId() + " joined room: " + roomName);
        });

        server.start();
        System.out.println("Socket.IO server started on " + host + ":" + port);
        return server;
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketIOServer) {
        return new SpringAnnotationScanner(socketIOServer);
    }

    @PreDestroy
    public void stopSocketIOServer() {
        if (server != null) {
            server.stop();
            System.out.println("Socket.IO server stopped.");
        }
    }
}
