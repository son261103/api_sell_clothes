package com.example.api_sell_clothes.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForceResetPasswordRequest {
    private String targetUsername;
    private String newPassword;
}