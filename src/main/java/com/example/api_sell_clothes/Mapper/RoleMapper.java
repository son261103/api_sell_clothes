package com.example.api_sell_clothes.Mapper;

import com.example.api_sell_clothes.DTO.RolesDTO;
import com.example.api_sell_clothes.Entity.Permissions;
import com.example.api_sell_clothes.Entity.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleMapper implements EntityMapper<Roles, RolesDTO> {
    @Override
    public Roles toEntity(RolesDTO dto) {
        if (dto == null) {
            return null;
        }
        Roles roles = Roles.builder()
                .roleId(dto.getRoleId())
                .roleName(dto.getRoleName())
                .roleDescription(dto.getRoleDescription())
                .build();
        if (dto.getPermissions() != null) {
            roles.setPermissions(dto.getPermissions().stream()
                    .map(permissionName -> Permissions.builder().permissionName(permissionName).build())
                    .collect(Collectors.toSet()));
        }
        return roles;
    }

    @Override
    public RolesDTO toDto(Roles entity) {
        if (entity == null) {
            return null;
        }
        RolesDTO rolesDTO = RolesDTO.builder()
                .roleId(entity.getRoleId())
                .roleName(entity.getRoleName())
                .roleDescription(entity.getRoleDescription())
                .build();
        if (entity.getPermissions() != null) {
            rolesDTO.setPermissions(entity.getPermissions().stream()
                    .map(Permissions::getPermissionName)
                    .collect(Collectors.toSet()));
        }
        return rolesDTO;
    }

    @Override
    public List<Roles> toEntity(List<RolesDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<RolesDTO> toDto(List<Roles> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Set<Roles> toEntitySet(Set<RolesDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());
    }

    public Set<RolesDTO> toDtoSet(Set<Roles> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }
}
