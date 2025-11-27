package com.exemplo.seguranca.api;

/**
 * DTO para resposta de autenticação
 */
public class AuthResponse {

    private String token;
    private String message;
    private Long userId;
    private String role;

    public AuthResponse() {
    }

    public AuthResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    public AuthResponse(String token, String message, Long userId, String role) {
        this.token = token;
        this.message = message;
        this.userId = userId;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
