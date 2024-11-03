package com.example.api_sell_clothes.Controller;

import com.example.api_sell_clothes.DTO.Auth.ChangePasswordRequest;
import com.example.api_sell_clothes.DTO.UsersDTO;
import com.example.api_sell_clothes.Exception.AuthException.ResourceNotFoundException;
import com.example.api_sell_clothes.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UsersDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsersDTO> getUserById(@PathVariable Long id) {
        UsersDTO user = userService.getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id);
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UsersDTO> getUserByUsername(@PathVariable String username) {
        UsersDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UsersDTO> createUser(@Valid @RequestBody UsersDTO userDTO) {
        try {
            return ResponseEntity.ok(userService.createUser(userDTO));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Không thể tạo người dùng: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsersDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UsersDTO userDTO) {
        try {
            userDTO.setUserId(id);
            UsersDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id);
        }
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(id, request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Không thể thay đổi mật khẩu: " + e.getMessage());
        }
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> addRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        try {
            userService.addRoleToUser(userId, roleId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng hoặc vai trò");
        }
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        try {
            userService.removeRoleFromUser(userId, roleId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng hoặc vai trò");
        }
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsernameAvailability(@PathVariable String username) {
        return ResponseEntity.ok(!userService.existsByUsername(username));
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmailAvailability(@PathVariable String email) {
        return ResponseEntity.ok(!userService.existsByEmail(email));
    }
}