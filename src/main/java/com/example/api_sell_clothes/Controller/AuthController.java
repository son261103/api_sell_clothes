package com.example.api_sell_clothes.Controller;

import com.example.api_sell_clothes.DTO.Auth.*;
import com.example.api_sell_clothes.Exception.AuthException.ForbiddenException;
import com.example.api_sell_clothes.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth")
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
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterAdminRequest request) {
        try {
            AuthResponse response = authService.registerAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Admin registration failed: " + e.getMessage() + " / Đăng ký admin thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/register-super-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AuthResponse> registerSuperAdmin(@Valid @RequestBody RegisterSuperAdminRequest request) {
        try {
            AuthResponse response = authService.registerSuperAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Super admin registration failed: " + e.getMessage() + " / Đăng ký super admin thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Password reset failed: " + e.getMessage() + " / Đặt lại mật khẩu thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/force-reset-password")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> forceResetPassword(@Valid @RequestBody ForceResetPasswordRequest request) {
        try {
            authService.forceResetPassword(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Force password reset failed: " + e.getMessage() + " / Đặt lại mật khẩu bắt buộc thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/recover-super-admin")
    public ResponseEntity<Void> recoverSuperAdmin(@Valid @RequestBody SuperAdminRecoveryRequest request) {
        try {
            authService.recoverSuperAdminPassword(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Super admin recovery failed: " + e.getMessage() + " / Khôi phục super admin thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/change-super-admin-password")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> changeSuperAdminPassword(@Valid @RequestBody SuperAdminPasswordChangeRequest request) {
        try {
            authService.changeSuperAdminPasswordWithVerification(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Super admin password change failed: " + e.getMessage() + " / Thay đổi mật khẩu super admin thất bại: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-admin/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long userId) {
        try {
            authService.deleteAdmin(userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Admin deletion failed: " + e.getMessage() + " / Xóa admin thất bại: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-super-admin/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteSuperAdmin(
            @PathVariable Long userId,
            @RequestParam String confirmPassword) {
        try {
            authService.deleteSuperAdmin(userId, confirmPassword);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Super admin deletion failed: " + e.getMessage() + " / Xóa super admin thất bại: " + e.getMessage());
        }
    }

    @PutMapping("/update-roles/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> updateUserRoles(
            @PathVariable Long userId,
            @RequestBody Set<String> roleNames) {
        try {
            authService.updateUserRoles(userId, roleNames);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Role update failed: " + e.getMessage() + " / Cập nhật vai trò thất bại: " + e.getMessage());
        }
    }

    @PutMapping("/disable-user/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> disableUser(@PathVariable Long userId) {
        try {
            authService.disableUser(userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("User disable failed: " + e.getMessage() + " / Vô hiệu hóa người dùng thất bại: " + e.getMessage());
        }
    }

    @PutMapping("/enable-user/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> enableUser(@PathVariable Long userId) {
        try {
            authService.enableUser(userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("User enable failed: " + e.getMessage() + " / Kích hoạt người dùng thất bại: " + e.getMessage());
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