package com.example.api_sell_clothes.Service;

import com.example.api_sell_clothes.DTO.UsersDTO;
import com.example.api_sell_clothes.DTO.Auth.*;
import com.example.api_sell_clothes.Entity.Users;
import com.example.api_sell_clothes.Entity.Roles;
import com.example.api_sell_clothes.Exception.AuthException.ResourceNotFoundException;
import com.example.api_sell_clothes.Mapper.UserMapper;
import com.example.api_sell_clothes.Repository.UserRepository;
import com.example.api_sell_clothes.Utils.AuthValidationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_SUPER_ADMIN = "super admin";

    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        AuthValidationUtils.validateAuthRequest(request);

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
        try {
            AuthValidationUtils.validateRegisterRequest(request);
            validateUniqueUser(request.getUsername(), request.getEmail());

            Users user = buildUserFromBasicRequest(
                    request.getUsername(),
                    request.getPassword(),
                    request.getEmail(),
                    request.getFullName(),
                    request.getPhone()
            );
            roleService.addDefaultRole(user);
            Users savedUser = userRepository.save(user);

            String token = jwtService.generateToken(savedUser);
            String refreshToken = jwtService.generateRefreshToken(savedUser);

            return buildAuthResponse(savedUser, token, refreshToken);
        } catch (Exception e) {
            throw e;
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public AuthResponse registerAdmin(@Valid RegisterAdminRequest request) {
        AuthValidationUtils.validateRegisterAdminRequest(request);
        validateUniqueUser(request.getUsername(), request.getEmail());

        Users user = buildUserFromBasicRequest(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getFullName(),
                request.getPhone()
        );
        roleService.addAdminRole(user);
        Users savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return buildAuthResponse(savedUser, token, refreshToken);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public AuthResponse registerSuperAdmin(@Valid RegisterSuperAdminRequest request) {
        AuthValidationUtils.validateRegisterSuperAdminRequest(request);
        validateUniqueUser(request.getUsername(), request.getEmail());

        Users user = buildUserFromBasicRequest(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getFullName(),
                request.getPhone()
        );
        roleService.addSuperAdminRole(user);
        Users savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return buildAuthResponse(savedUser, token, refreshToken);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        Users user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        AuthValidationUtils.validateResetPassword(request, user);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void forceResetPassword(ForceResetPasswordRequest request) {
        Users targetUser = userRepository.findByUsername(request.getTargetUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        AuthValidationUtils.validateForceResetPassword(request);

        // Không cho phép force reset mật khẩu của super admin mặc định
        if (DEFAULT_SUPER_ADMIN.equals(targetUser.getUsername())) {
            throw new IllegalArgumentException("Không thể thay đổi mật khẩu của tài khoản Super Admin mặc định");
        }

        targetUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        targetUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(targetUser);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void deleteAdmin(Long userId) {
        Users admin = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy admin"));

        AuthValidationUtils.validateDeleteAdmin(admin);

        if (!roleService.hasAdminRole(admin)) {
            throw new IllegalArgumentException("Người dùng không phải là Admin");
        }

        userRepository.delete(admin);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void deleteSuperAdmin(Long userId, String confirmPassword) {
        Users superAdmin = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Super Admin"));

        AuthValidationUtils.validateDeleteSuperAdmin(superAdmin);

        if (!roleService.hasSuperAdminRole(superAdmin)) {
            throw new IllegalArgumentException("Người dùng không phải là Super Admin");
        }

        // Kiểm tra số lượng Super Admin
        long superAdminCount = userRepository.countSuperAdmins();
        if (superAdminCount <= 1) {
            throw new IllegalArgumentException("Không thể xóa Super Admin cuối cùng");
        }

        if (!passwordEncoder.matches(confirmPassword, superAdmin.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không chính xác");
        }

        userRepository.delete(superAdmin);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public void updateUserRoles(Long userId, Set<String> roleNames) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        // Kiểm tra đặc biệt cho vai trò Super Admin
        boolean hasSuperAdmin = user.getRoles().stream()
                .anyMatch(role -> "SUPER_ADMIN".equals(role.getRoleName()));
        boolean willHaveSuperAdmin = roleNames.contains("SUPER_ADMIN");

        if (hasSuperAdmin && !willHaveSuperAdmin) {
            // Kiểm tra nếu đây là Super Admin cuối cùng
            long superAdminCount = userRepository.countSuperAdmins();
            if (superAdminCount <= 1) {
                throw new IllegalArgumentException("Không thể xóa vai trò Super Admin cuối cùng");
            }
        }

        Set<Roles> newRoles = roleNames.stream()
                .map(roleName -> roleService.findByRoleName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(newRoles);
        userRepository.save(user);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @Transactional
    public void disableUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        // Không cho phép vô hiệu hóa super admin mặc định
        if (DEFAULT_SUPER_ADMIN.equals(user.getUsername())) {
            throw new IllegalArgumentException("Không thể vô hiệu hóa tài khoản Super Admin mặc định");
        }

        // Kiểm tra nếu đây là Super Admin cuối cùng
        if (roleService.hasSuperAdminRole(user)) {
            long superAdminCount = userRepository.countSuperAdmins();
            if (superAdminCount <= 1) {
                throw new IllegalArgumentException("Không thể vô hiệu hóa Super Admin cuối cùng");
            }
        }

        user.setEnabled(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @Transactional
    public void enableUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        user.setEnabled(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
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

    private Users buildUserFromBasicRequest(String username, String password, String email, String fullName, String phone) {
        return Users.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .phone(phone)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private AuthResponse buildAuthResponse(Users user, String token, String refreshToken) {
        UsersDTO userDTO = userMapper.toDto(user);
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName())
                .collect(Collectors.toList());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .fullName(userDTO.getFullName())
                .roles(roles)
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

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void recoverSuperAdminPassword(SuperAdminRecoveryRequest request) {
        Users superAdmin = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Super Admin"));

        // Validate thông tin recovery
        AuthValidationUtils.validateSuperAdminRecovery(request, superAdmin);

        // Kiểm tra thông tin xác thực
        if (!request.getEmail().equals(superAdmin.getEmail())) {
            throw new IllegalArgumentException("Email không chính xác");
        }

        // Kiểm tra có phải Super Admin không
        if (!roleService.hasSuperAdminRole(superAdmin)) {
            throw new IllegalArgumentException("Tài khoản không phải là Super Admin");
        }

        // Cập nhật mật khẩu mới
        superAdmin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        superAdmin.setUpdatedAt(LocalDateTime.now());
        userRepository.save(superAdmin);
    }

    /**
     * Xác thực và đổi mật khẩu cho Super Admin sau khi xác minh
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void changeSuperAdminPasswordWithVerification(SuperAdminPasswordChangeRequest request) {
        Users superAdmin = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Super Admin"));

        // Validate request
        AuthValidationUtils.validateSuperAdminPasswordChange(request, superAdmin);

        // Kiểm tra email và ngày sinh
        if (!request.getEmail().equals(superAdmin.getEmail())) {
            throw new IllegalArgumentException("Email không chính xác");
        }

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), superAdmin.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác");
        }

        // Kiểm tra có phải Super Admin không
        if (!roleService.hasSuperAdminRole(superAdmin)) {
            throw new IllegalArgumentException("Tài khoản không phải là Super Admin");
        }

        // Đảm bảo không phải Super Admin mặc định hoặc có xác nhận đặc biệt
        if (DEFAULT_SUPER_ADMIN.equals(superAdmin.getUsername()) && !request.isConfirmedDefaultSuperAdmin()) {
            throw new IllegalArgumentException("Cần xác nhận đặc biệt để đổi mật khẩu Super Admin mặc định");
        }

        // Cập nhật mật khẩu mới
        superAdmin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        superAdmin.setUpdatedAt(LocalDateTime.now());
        userRepository.save(superAdmin);
    }
}