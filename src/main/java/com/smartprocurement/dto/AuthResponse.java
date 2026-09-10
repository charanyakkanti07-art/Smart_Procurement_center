package com.smartprocurement.dto;

import com.smartprocurement.entity.Role;

public class AuthResponse {
    private String token;
    private String message;
    private Long farmerId;
    private Long userId;
    private Role role;
    private Long centreId;
    private String centreName;

    public AuthResponse() {
    }

    public AuthResponse(String token, String message, Long farmerId, Long userId, Role role, Long centreId, String centreName) {
        this.token = token;
        this.message = message;
        this.farmerId = farmerId;
        this.userId = userId;
        this.role = role;
        this.centreId = centreId;
        this.centreName = centreName;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getCentreId() { return centreId; }
    public void setCentreId(Long centreId) { this.centreId = centreId; }

    public String getCentreName() { return centreName; }
    public void setCentreName(String centreName) { this.centreName = centreName; }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String token;
        private String message;
        private Long farmerId;
        private Long userId;
        private Role role;
        private Long centreId;
        private String centreName;

        public AuthResponseBuilder token(String token) { this.token = token; return this; }
        public AuthResponseBuilder message(String message) { this.message = message; return this; }
        public AuthResponseBuilder farmerId(Long farmerId) { this.farmerId = farmerId; return this; }
        public AuthResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public AuthResponseBuilder role(Role role) { this.role = role; return this; }
        public AuthResponseBuilder centreId(Long centreId) { this.centreId = centreId; return this; }
        public AuthResponseBuilder centreName(String centreName) { this.centreName = centreName; return this; }

        public AuthResponse build() {
            return new AuthResponse(token, message, farmerId, userId, role, centreId, centreName);
        }
    }
}
