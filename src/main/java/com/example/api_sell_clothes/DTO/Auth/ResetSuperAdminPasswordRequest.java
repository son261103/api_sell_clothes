package com.example.api_sell_clothes.DTO.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetSuperAdminPasswordRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "New password is required")
    private String newPassword;
}