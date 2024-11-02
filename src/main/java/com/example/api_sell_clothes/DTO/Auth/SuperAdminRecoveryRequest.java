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
public class SuperAdminRecoveryRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, message = "Tên đăng nhập phải có ít nhất 3 ký tự")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$",
            message = "Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt")
    private String newPassword;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải có 10 chữ số")
    private String phoneNumber;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, message = "Họ tên phải có ít nhất 2 ký tự")
    private String fullName;

    @NotNull(message = "Ngày sinh không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Câu hỏi bảo mật không được để trống")
    private String securityQuestion;

    @NotBlank(message = "Câu trả lời bảo mật không được để trống")
    private String securityAnswer;

    @NotBlank(message = "Mã xác nhận không được để trống")
    private String recoveryCode;

    private String backupEmail;  // Email dự phòng (không bắt buộc)

    @Builder.Default
    private boolean agreeToTerms = false;  // Đồng ý với điều khoản
}