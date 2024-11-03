package com.example.api_sell_clothes.Service;

import com.example.api_sell_clothes.DTO.PermissionsDTO;
import com.example.api_sell_clothes.DTO.RolesDTO;
import com.example.api_sell_clothes.Entity.Permissions;
import com.example.api_sell_clothes.Entity.Roles;
import com.example.api_sell_clothes.Entity.Users;
import com.example.api_sell_clothes.Exception.AuthException.ResourceNotFoundException;
import com.example.api_sell_clothes.Mapper.PermissionMapper;
import com.example.api_sell_clothes.Mapper.RoleMapper;
import com.example.api_sell_clothes.Repository.PermissionRepository;
import com.example.api_sell_clothes.Repository.RoleRepository;
import com.example.api_sell_clothes.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public RolesDTO getRoleById(Long id) {
        Roles role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò / Role not found"));
        return roleMapper.toDto(role);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public RolesDTO getRoleByName(String name) {
        Roles role = roleRepository.findByRoleName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò / Role not found"));
        return roleMapper.toDto(role);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public List<RolesDTO> getAllRoles() {
        List<Roles> roles = roleRepository.findAll();
        return roleMapper.toDto(roles);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Set<PermissionsDTO> getRolePermissions(Long roleId) {
        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò / Role not found"));
        return role.getPermissions().stream()
                .map(permissionMapper::toDto)
                .collect(Collectors.toSet());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public RolesDTO createRole(RolesDTO roleDTO) {
        if (roleRepository.existsByRoleName(roleDTO.getRoleName())) {
            throw new RuntimeException("Tên vai trò đã tồn tại / Role name already exists");
        }

        Roles role = roleMapper.toEntity(roleDTO);

        if (roleDTO.getPermissions() != null && !roleDTO.getPermissions().isEmpty()) {
            Set<Permissions> permissions = roleDTO.getPermissions().stream()
                    .map(permName -> permissionRepository.findByPermissionName(permName)
                            .orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + permName)))
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }

        Roles savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public RolesDTO updateRole(Long id, RolesDTO roleDTO) {
        Roles existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò với id: " + id));

        if (!existingRole.getRoleName().equals(roleDTO.getRoleName())
                && roleRepository.existsByRoleName(roleDTO.getRoleName())) {
            throw new IllegalArgumentException("Tên vai trò đã tồn tại");
        }

        existingRole.setRoleName(roleDTO.getRoleName());
        existingRole.setRoleDescription(roleDTO.getRoleDescription());

        if (roleDTO.getPermissions() != null) {
            Set<Permissions> newPermissions = roleDTO.getPermissions().stream()
                    .map(permName -> permissionRepository.findByPermissionName(permName)
                            .orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + permName)))
                    .collect(Collectors.toSet());
            existingRole.setPermissions(newPermissions);
        }

        Roles savedRole = roleRepository.save(existingRole);
        return roleMapper.toDto(savedRole);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy vai trò / Role not found");
        }
        roleRepository.deleteById(id);
    }

    @PreAuthorize("permitAll()")
    @Transactional
    public void addDefaultRole(Users user) {
        Roles defaultRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò mặc định / Default role not found"));
        addRoleToUser(user, defaultRole);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public void addAdminRole(Users user) {
        Roles adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò Admin / Admin role not found"));
        addRoleToUser(user, adminRole);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void addSuperAdminRole(Users user) {
        Roles superAdminRole = roleRepository.findByRoleName("SUPER_ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò Super Admin / Super Admin role not found"));
        addRoleToUser(user, superAdminRole);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void addMultipleRolesToUser(Long userId, List<String> roleNames) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng / User not found"));

        for (String roleName : roleNames) {
            Roles role = roleRepository.findByRoleName(roleName.toUpperCase())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò: " + roleName));
            addRoleToUser(user, role);
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void addPermissionToRole(Long roleId, Long permissionId) {
        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò với id: " + roleId));

        Permissions permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quyền với id: " + permissionId));

        if (role.getPermissions() == null) {
            role.setPermissions(new HashSet<>());
        }
        // Tiếp tục phần RoleService.java

        if (role.getPermissions().contains(permission)) {
            throw new IllegalArgumentException("Quyền đã tồn tại trong vai trò này");
        }

        role.getPermissions().add(permission);
        roleRepository.save(role);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void updateRolePermissions(Long roleId, List<Long> permissionIds) {
        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò / Role not found"));

        Set<Permissions> newPermissions = permissionIds.stream()
                .map(id -> permissionRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quyền với id: " + id)))
                .collect(Collectors.toSet());

        role.setPermissions(newPermissions);
        roleRepository.save(role);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò với id: " + roleId));

        Permissions permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quyền với id: " + permissionId));

        if (role.getPermissions() == null || !role.getPermissions().contains(permission)) {
            throw new IllegalArgumentException("Quyền không tồn tại trong vai trò này");
        }

        role.getPermissions().remove(permission);
        roleRepository.save(role);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void updatePermissionInRole(Long roleId, Long permissionId, PermissionsDTO updatedPermission) {
        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò / Role not found"));

        Permissions existingPermission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quyền / Permission not found"));

        if (!role.getPermissions().contains(existingPermission)) {
            throw new IllegalArgumentException("Quyền không tồn tại trong vai trò này");
        }

        // Cập nhật thông tin quyền
        existingPermission.setPermissionName(updatedPermission.getPermissionName());
        existingPermission.setPermissionDescription(updatedPermission.getPermissionDescription());

        permissionRepository.save(existingPermission);
    }

    public boolean hasSuperAdminRole(Users user) {
        return user.getRoles() != null && user.getRoles().stream()
                .anyMatch(role -> "SUPER_ADMIN".equals(role.getRoleName()));
    }

    public Optional<Roles> findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    private void addRoleToUser(Users user, Roles role) {
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }

    @PostConstruct
    public void initRoles() {
        // Khởi tạo vai trò mặc định
        addRoleIfNotExists("USER", "Basic user role with standard permissions");
        addRoleIfNotExists("ADMIN", "Administrator role with elevated privileges");
        addRoleIfNotExists("SUPER_ADMIN", "Super administrator with full system access");
    }

    private void addRoleIfNotExists(String roleName, String roleDescription) {
        if (!roleRepository.existsByRoleName(roleName)) {
            Roles role = new Roles();
            role.setRoleName(roleName);
            role.setRoleDescription(roleDescription);
            role.setPermissions(new HashSet<>());
            roleRepository.save(role);
        }
    }

    public boolean hasAdminRole(Users user) {
        return user.getRoles() != null && user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getRoleName()));
    }

}
