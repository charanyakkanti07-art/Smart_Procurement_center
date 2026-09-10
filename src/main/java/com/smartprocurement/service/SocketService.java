package com.smartprocurement.service;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocketService {

    @Autowired
    private SocketIOServer socketIOServer;

    public void broadcastQueueUpdate(Long centreId, Object payload) {
        String roomName = "centre_" + centreId;
        socketIOServer.getRoomOperations(roomName).sendEvent("queue:updated", payload);
        socketIOServer.getBroadcastOperations().sendEvent("queue:updated", payload);
    }

    public void broadcastFarmerCalled(Long centreId, Object payload) {
        String roomName = "centre_" + centreId;
        socketIOServer.getRoomOperations(roomName).sendEvent("farmer:called", payload);
    }

    public void broadcastFarmerProcessing(Long centreId, Object payload) {
        String roomName = "centre_" + centreId;
        socketIOServer.getRoomOperations(roomName).sendEvent("farmer:processing", payload);
    }

    public void broadcastFarmerCompleted(Long centreId, Object payload) {
        String roomName = "centre_" + centreId;
        socketIOServer.getRoomOperations(roomName).sendEvent("farmer:completed", payload);
    }

    public void broadcastFarmerCancelled(Long centreId, Object payload) {
        String roomName = "centre_" + centreId;
        socketIOServer.getRoomOperations(roomName).sendEvent("farmer:cancelled", payload);
    }

    public void broadcastNotification(Long farmerId, Object payload) {
        if (socketIOServer != null) {
            try {
                socketIOServer.getBroadcastOperations().sendEvent("notification:received", payload);
            } catch (Exception ignored) {
            }
        }
    }
}
