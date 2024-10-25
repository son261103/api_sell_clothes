package com.example.api_sell_clothes.Controller;

import com.example.api_sell_clothes.DTO.Auth.*;
import com.example.api_sell_clothes.Exception.ForbiddenException;
import com.example.api_sell_clothes.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Invalid username or password / Tên đăng nhập hoặc mật khẩu không hợp lệ");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Registration failed: " + e.getMessage() + " / Đăng ký thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterAdminRequest request) {
        try {
            AuthResponse response = authService.registerAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Admin registration failed: " + e.getMessage() + " / Đăng ký admin thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/register-super-admin")
    public ResponseEntity<AuthResponse> registerSuperAdmin(@Valid @RequestBody RegisterSuperAdminRequest request) {
        try {
            AuthResponse response = authService.registerSuperAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Super admin registration failed: " + e.getMessage() + " / Đăng ký super admin thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/reset-super-admin-password")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> resetSuperAdminPassword(@Valid @RequestBody ResetSuperAdminPasswordRequest request) {
        try {
            authService.resetSuperAdminPassword(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Super admin password reset failed: " + e.getMessage() + " / Đặt lại mật khẩu super admin thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Invalid refresh token / Token làm mới không hợp lệ");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        try {
            authService.logout(token);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Logout failed: " + e.getMessage() + " / Đăng xuất thất bại: " + e.getMessage());
        }
    }
}
