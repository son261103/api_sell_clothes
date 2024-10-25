package com.example.api_sell_clothes.Service;

import com.example.api_sell_clothes.DTO.Auth.ChangePasswordRequest;
import com.example.api_sell_clothes.DTO.UsersDTO;
import com.example.api_sell_clothes.Entity.Roles;
import com.example.api_sell_clothes.Entity.Users;
import com.example.api_sell_clothes.Exception.ResourceNotFoundException;
import com.example.api_sell_clothes.Mapper.UserMapper;
import com.example.api_sell_clothes.Repository.RoleRepository;
import com.example.api_sell_clothes.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    // Lấy tất cả người dùng
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public List<UsersDTO> getAllUsers() {
        List<Users> users = userRepository.findAll();
        return userMapper.toDto(users);
    }

    // Lấy người dùng theo ID
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public UsersDTO getUserById(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id));
        return userMapper.toDto(user);
    }

    // Lấy người dùng theo tên
    public UsersDTO getUserByUsername(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với username: " + username));
        return userMapper.toDto(user);
    }

    // Tạo người dùng mới
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public UsersDTO createUser(UsersDTO userDTO) {
        validateUser(userDTO);

        Users user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Thêm vai trò mặc định
        roleService.addDefaultRole(user);

        Users savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    // Cập nhật thông tin người dùng
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public UsersDTO updateUser(Long id, UsersDTO userDTO) {
        Users existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id));

        if (!existingUser.getUsername().equals(userDTO.getUsername())
                && userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Tên người dùng đã tồn tại / Username already exists");
        }
        if (!existingUser.getEmail().equals(userDTO.getEmail())
                && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng / Email is already in use");
        }

        updateUserDetails(existingUser, userDTO);
        Users updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    // Xóa người dùng
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id);
        }
        userRepository.deleteById(id);
    }

    // Thay đổi mật khẩu
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + userId));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác / Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Kiểm tra sự tồn tại của tên người dùng
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // Kiểm tra sự tồn tại của email
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Thêm vai trò cho người dùng
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public void addRoleToUser(Long userId, Long roleId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + userId));
        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò với id: " + roleId));

        // Thêm vai trò vào người dùng
        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
        } else {
            throw new IllegalArgumentException("Vai trò này đã tồn tại cho người dùng");
        }
        userRepository.save(user);
    }

    // Xóa vai trò khỏi người dùng
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + userId));
        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò với id: " + roleId));

        // Xóa vai trò khỏi người dùng
        if (user.getRoles().remove(role)) {
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Người dùng không có vai trò này");
        }
    }

    // Kiểm tra và xác thực người dùng
    private void validateUser(UsersDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Tên người dùng đã tồn tại / Username already exists");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng / Email is already in use");
        }
    }

    // Cập nhật chi tiết người dùng
    private void updateUserDetails(Users existingUser, UsersDTO userDTO) {
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setFullName(userDTO.getFullName());
        existingUser.setPhone(userDTO.getPhone());
        existingUser.setUpdatedAt(LocalDateTime.now());
    }
}
