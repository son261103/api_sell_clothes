package com.example.api_sell_clothes.Service;

import com.example.api_sell_clothes.DTO.Auth.ChangePasswordRequest;
import com.example.api_sell_clothes.DTO.UsersDTO;
import com.example.api_sell_clothes.Entity.Roles;
import com.example.api_sell_clothes.Entity.Users;
import com.example.api_sell_clothes.Exception.AuthException.ResourceNotFoundException;
import com.example.api_sell_clothes.Mapper.UserMapper;
import com.example.api_sell_clothes.Repository.RoleRepository;
import com.example.api_sell_clothes.Repository.UserRepository;
import com.example.api_sell_clothes.Utils.UserValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public List<UsersDTO> getAllUsers() {
        List<Users> users = userRepository.findAll();
        return userMapper.toDto(users);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public UsersDTO getUserById(Long id) {
        Users user = findUserById(id);
        return userMapper.toDto(user);
    }


    public UsersDTO getUserByUsername(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với username: " + username));
        return userMapper.toDto(user);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public UsersDTO createUser(UsersDTO userDTO) {
        // Validate thông tin người dùng mới
        UserValidationUtils.validateNewUser(userDTO, userRepository);

        // Tạo entity từ DTO
        Users user = userMapper.toEntity(userDTO);

        // Thiết lập các giá trị mặc định
        initializeNewUser(user, userDTO);

        // Lưu người dùng và chuyển đổi kết quả về DTO
        Users savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public UsersDTO updateUser(Long id, UsersDTO userDTO) {
        Users existingUser = findUserById(id);

        // Validate thông tin cập nhật
        UserValidationUtils.validateUserUpdate(userDTO, existingUser, userRepository);

        // Cập nhật thông tin
        updateUserDetails(existingUser, userDTO);

        // Cập nhật roles nếu có
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            updateUserRoles(existingUser, userDTO.getRoles());
        }

        Users updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        Users user = findUserById(userId);

        // Validate thông tin đổi mật khẩu
        UserValidationUtils.validatePasswordChange(user, request, passwordEncoder);

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public void addRoleToUser(Long userId, Long roleId) {
        Users user = findUserById(userId);
        Roles role = findRoleById(roleId);

        if (user.getRoles().contains(role)) {
            throw new IllegalArgumentException("Người dùng đã có vai trò này");
        }

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        Users user = findUserById(userId);
        Roles role = findRoleById(roleId);

        if (!user.getRoles().remove(role)) {
            throw new IllegalArgumentException("Người dùng không có vai trò này");
        }

        userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Private helper methods

    private Users findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id));
    }

    private Roles findRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò với id: " + id));
    }

    private void initializeNewUser(Users user, UsersDTO userDTO) {
        // Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Thiết lập thời gian
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Thiết lập trạng thái
        user.setEnabled(userDTO.getEnabled() != null ? userDTO.getEnabled() : true);

        // Thêm vai trò mặc định
        roleService.addDefaultRole(user);
    }

    private void updateUserDetails(Users existingUser, UsersDTO userDTO) {
        if (userDTO.getUsername() != null) {
            existingUser.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            existingUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getFullName() != null) {
            existingUser.setFullName(userDTO.getFullName());
        }
        if (userDTO.getPhone() != null) {
            existingUser.setPhone(userDTO.getPhone());
        }
        if (userDTO.getEnabled() != null) {
            existingUser.setEnabled(userDTO.getEnabled());
        }
        existingUser.setUpdatedAt(LocalDateTime.now());
    }

    private void updateUserRoles(Users user, Set<String> roleNames) {
        Set<Roles> newRoles = roleNames.stream()
                .map(roleName -> roleRepository.findByRoleName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò: " + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(newRoles);
    }
}