package com.exemplo.seguranca.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record LoginResponse(
    @JsonProperty("access_token") String token,
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("expires_in") Instant expiresIn,
    UserResponse user
) {
    public LoginResponse(String token, UserResponse user, Instant expirationDate) {
        this(token, "Bearer", expirationDate, user);
    }
}