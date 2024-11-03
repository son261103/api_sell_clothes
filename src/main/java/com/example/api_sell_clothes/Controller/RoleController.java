package com.example.api_sell_clothes.Controller;

import com.example.api_sell_clothes.DTO.PermissionsDTO;
import com.example.api_sell_clothes.DTO.RolesDTO;
import com.example.api_sell_clothes.Entity.Users;
import com.example.api_sell_clothes.Exception.AuthException.ResourceNotFoundException;
import com.example.api_sell_clothes.Service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RolesDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolesDTO> getRoleById(@PathVariable Long id) {
        RolesDTO role = roleService.getRoleById(id);
        if (role == null) {
            throw new ResourceNotFoundException("Không tìm thấy vai trò với id: " + id + " / Role not found with id: " + id);
        }
        return ResponseEntity.ok(role);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<RolesDTO> getRoleByName(@PathVariable String name) {
        RolesDTO role = roleService.getRoleByName(name);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<Set<PermissionsDTO>> getRolePermissions(@PathVariable Long roleId) {
        Set<PermissionsDTO> permissions = roleService.getRolePermissions(roleId);
        return ResponseEntity.ok(permissions);
    }

    @PostMapping
    public ResponseEntity<RolesDTO> createRole(@Valid @RequestBody RolesDTO roleDTO) {
        try {
            return ResponseEntity.ok(roleService.createRole(roleDTO));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Không thể tạo vai trò: " + e.getMessage() + " / Cannot create role: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolesDTO> updateRole(@PathVariable Long id, @Valid @RequestBody RolesDTO roleDTO) {
        try {
            roleDTO.setRoleId(id);
            RolesDTO updatedRole = roleService.updateRole(id, roleDTO);
            return ResponseEntity.ok(updatedRole);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Không tìm thấy vai trò với id: " + id + " / Role not found with id: " + id);
        }
    }

    @PostMapping("/addDefaultRole/{userId}")
    public ResponseEntity<Void> addDefaultRoleToUser(@PathVariable Long userId) {
        roleService.addDefaultRole(new Users(userId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addAdminRole/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> addAdminRoleToUser(@PathVariable Long userId) {
        roleService.addAdminRole(new Users(userId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addSuperAdminRole/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> addSuperAdminRoleToUser(@PathVariable Long userId) {
        roleService.addSuperAdminRole(new Users(userId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addRoles/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> addMultipleRolesToUser(@PathVariable Long userId, @RequestBody List<String> roleNames) {
        try {
            roleService.addMultipleRolesToUser(userId, roleNames);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi khi thêm vai trò: " + e.getMessage());
        }
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> addPermissionToRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        try {
            roleService.addPermissionToRole(roleId, permissionId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Không tìm thấy vai trò hoặc quyền / Role or permission not found");
        }
    }

    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> updateRolePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        roleService.updateRolePermissions(roleId, permissionIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> removePermissionFromRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        try {
            roleService.removePermissionFromRole(roleId, permissionId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Không tìm thấy vai trò hoặc quyền / Role or permission not found");
        }
    }

    @PutMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> updatePermissionInRole(@PathVariable Long roleId, @PathVariable Long permissionId, @RequestBody PermissionsDTO updatedPermission) {
        roleService.updatePermissionInRole(roleId, permissionId, updatedPermission);
        return ResponseEntity.ok().build();
    }
}