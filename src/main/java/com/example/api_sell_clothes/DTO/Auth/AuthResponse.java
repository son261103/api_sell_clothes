package com.example.api_sell_clothes.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Set<String> roles;

    // Constructor cơ bản cho trường hợp trả về chỉ token và thông tin cần thiết
    public AuthResponse(String token, String refreshToken, String username) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.username = username;
    }

    // Constructor đầy đủ nếu cần thông tin chi tiết hơn
    public AuthResponse(String token, String refreshToken, String username, String fullName, String email, Set<String> roles) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
    }

    // Phương thức để ẩn thông tin nhạy cảm (email, fullName, roles)
    public void hideSensitiveInfo() {
        this.email = null;
        this.fullName = null;
        this.roles = null;
    }
}
