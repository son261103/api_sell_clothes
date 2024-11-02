package com.example.api_sell_clothes.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private String refreshToken;
    private String username;
    private String fullName;
    private String email;
    private List<String> roles;
}
