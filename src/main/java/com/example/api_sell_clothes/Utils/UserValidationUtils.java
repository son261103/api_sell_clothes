package com.example.api_sell_clothes.Utils;

import com.example.api_sell_clothes.DTO.Auth.ChangePasswordRequest;
import com.example.api_sell_clothes.DTO.UsersDTO;
import com.example.api_sell_clothes.Entity.Users;
import com.example.api_sell_clothes.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserValidationUtils {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_PATTERN = "^[0-9]{10}$";
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_USERNAME_LENGTH = 3;

    // Validate cho việc tạo user mới
    public static void validateNewUser(UsersDTO userDTO, UserRepository userRepository) {
        validateBasicUserFields(userDTO);
        validateUniqueFields(userDTO, userRepository, null);
        validatePassword(userDTO.getPassword());
    }

    // Validate cho việc cập nhật user
    public static void validateUserUpdate(UsersDTO userDTO, Users existingUser, UserRepository userRepository) {
        if (userDTO.getUsername() != null) {
            validateUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            validateEmail(userDTO.getEmail());
        }
        if (userDTO.getPhone() != null) {
            validatePhone(userDTO.getPhone());
        }
        validateUniqueFields(userDTO, userRepository, existingUser.getUserId());
    }

    // Validate cho việc thay đổi mật khẩu
    public static void validatePasswordChange(Users user, ChangePasswordRequest request, PasswordEncoder passwordEncoder) {
        if (request.getOldPassword() == null || request.getNewPassword() == null) {
            throw new IllegalArgumentException("Mật khẩu cũ và mới không được để trống");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác");
        }

        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới phải khác mật khẩu cũ");
        }

        validatePassword(request.getNewPassword());
    }

    // Validate các trường cơ bản
    private static void validateBasicUserFields(UsersDTO userDTO) {
        if (userDTO == null) {
            throw new IllegalArgumentException("Dữ liệu người dùng không được để trống");
        }

        validateUsername(userDTO.getUsername());
        validateEmail(userDTO.getEmail());

        if (userDTO.getPhone() != null) {
            validatePhone(userDTO.getPhone());
        }
    }

    // Validate username
    private static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên người dùng không được để trống");
        }
        if (username.length() < MIN_USERNAME_LENGTH) {
            throw new IllegalArgumentException("Tên người dùng phải có ít nhất " + MIN_USERNAME_LENGTH + " ký tự");
        }
        if (!username.matches("^[a-zA-Z0-9._-]{3,}$")) {
            throw new IllegalArgumentException("Tên người dùng chỉ được chứa chữ cái, số và các ký tự ._-");
        }
    }

    // Validate email
    private static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (!Pattern.compile(EMAIL_PATTERN).matcher(email).matches()) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
    }

    // Validate số điện thoại
    private static void validatePhone(String phone) {
        if (!Pattern.compile(PHONE_PATTERN).matcher(phone).matches()) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải có 10 chữ số)");
        }
    }

    // Validate mật khẩu
    private static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất một chữ hoa");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất một chữ thường");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất một chữ số");
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất một ký tự đặc biệt");
        }
    }

    // Validate các trường unique (username, email)
    private static void validateUniqueFields(UsersDTO userDTO, UserRepository userRepository, Long userId) {
        // Kiểm tra username
        if (userDTO.getUsername() != null && userRepository.existsByUsername(userDTO.getUsername())) {
            if (userId == null || !userRepository.findByUsername(userDTO.getUsername())
                    .map(Users::getUserId)
                    .filter(id -> id.equals(userId))
                    .isPresent()) {
                throw new IllegalArgumentException("Tên người dùng đã tồn tại");
            }
        }

        // Kiểm tra email
        if (userDTO.getEmail() != null && userRepository.existsByEmail(userDTO.getEmail())) {
            if (userId == null || !userRepository.findByEmail(userDTO.getEmail())
                    .map(Users::getUserId)
                    .filter(id -> id.equals(userId))
                    .isPresent()) {
                throw new IllegalArgumentException("Email đã được sử dụng");
            }
        }
    }
}