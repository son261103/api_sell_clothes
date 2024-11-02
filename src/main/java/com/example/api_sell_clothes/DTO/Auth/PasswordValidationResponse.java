package com.example.api_sell_clothes.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordValidationResponse {
    private boolean valid;
    private String message;
    private int strength;  // Độ mạnh của mật khẩu (1-5)
    private List<String> validationErrors;
}