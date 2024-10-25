package com.example.api_sell_clothes.Service;

import com.example.api_sell_clothes.DTO.UsersDTO;
import com.example.api_sell_clothes.DTO.Auth.*;
import com.example.api_sell_clothes.Entity.Users;
import com.example.api_sell_clothes.Exception.ResourceNotFoundException;
import com.example.api_sell_clothes.Mapper.UserMapper;
import com.example.api_sell_clothes.Repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            var user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            return buildAuthResponse(user, token, refreshToken);
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không đúng", e);
        }
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        Users user = Users.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        roleService.addDefaultRole(user);
        Users savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return buildAuthResponse(savedUser, token, refreshToken);
    }

    @Transactional
    public AuthResponse registerAdmin(@Valid RegisterAdminRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        Users user = Users.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        roleService.addAdminRole(user);
        Users savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return buildAuthResponse(savedUser, token, refreshToken);
    }

    @Transactional
    public AuthResponse registerSuperAdmin(@Valid RegisterSuperAdminRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        Users user = Users.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        roleService.addSuperAdminRole(user);
        Users savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return buildAuthResponse(savedUser, token, refreshToken);
    }

    @Transactional
    public void resetSuperAdminPassword(ResetSuperAdminPasswordRequest request) {
        Users superAdmin = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Super Admin"));

        if (!roleService.hasSuperAdminRole(superAdmin)) {
            throw new IllegalArgumentException("Người dùng không phải là Super Admin");
        }

        superAdmin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        superAdmin.setUpdatedAt(LocalDateTime.now());
        userRepository.save(superAdmin);
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (jwtService.isTokenValid(refreshToken)) {
            String username = jwtService.extractUsername(refreshToken);
            Users user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

            String newToken = jwtService.generateToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            return buildAuthResponse(user, newToken, newRefreshToken);
        }
        throw new IllegalArgumentException("Refresh token không hợp lệ");
    }

    @Transactional
    public void logout(String token) {
        jwtService.invalidateToken(token);
    }

    private AuthResponse buildAuthResponse(Users user, String token, String refreshToken) {
        UsersDTO userDTO = userMapper.toDto(user);
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .fullName(userDTO.getFullName())
                .build();
    }

    private void validateUniqueUser(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }
    }
}