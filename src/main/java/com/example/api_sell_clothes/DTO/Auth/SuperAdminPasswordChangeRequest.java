package com.example.api_sell_clothes.DTO.Auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdminPasswordChangeRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, message = "Tên đăng nhập phải có ít nhất 3 ký tự")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu cũ không được để trống")
    private String oldPassword;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$",
            message = "Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải có 10 chữ số")
    private String phoneNumber;

    @NotNull(message = "Ngày sinh không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Câu hỏi bảo mật không được để trống")
    private String securityQuestion;

    @NotBlank(message = "Câu trả lời bảo mật không được để trống")
    private String securityAnswer;

    @Builder.Default
    private boolean confirmedDefaultSuperAdmin = false;  // Xác nhận đặc biệt cho Super Admin mặc định

    @NotBlank(message = "Lý do thay đổi không được để trống")
    private String changeReason;  // Lý do thay đổi mật khẩu

    private String currentLocation;  // Vị trí hiện tại (không bắt buộc)

    @Builder.Default
    private boolean forcedChange = false;  // Đánh dấu nếu đây là thay đổi bắt buộc

    @Builder.Default
    private boolean agreeToTerms = false;  // Đồng ý với điều khoản
}