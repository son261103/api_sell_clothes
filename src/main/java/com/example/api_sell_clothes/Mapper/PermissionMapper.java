package com.example.api_sell_clothes.Mapper;

import com.example.api_sell_clothes.DTO.PermissionsDTO;
import com.example.api_sell_clothes.Entity.Permissions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionMapper implements EntityMapper<Permissions, PermissionsDTO> {
    @Override
    public Permissions toEntity(PermissionsDTO dto) {
        if (dto == null) {
            return null;
        }
        Permissions permissions = Permissions.builder()
                .permissionId(dto.getPermissionId())
                .permissionName(dto.getPermissionName())
                .permissionDescription(dto.getPermissionDescription())
                .build();
        return permissions;
    }

    @Override
    public PermissionsDTO toDto(Permissions entity) {
        if (entity == null) {
            return null;
        }
        PermissionsDTO permissionsDTO = PermissionsDTO.builder()
                .permissionId(entity.getPermissionId())
                .permissionName(entity.getPermissionName())
                .permissionDescription(entity.getPermissionDescription())
                .build();
        return permissionsDTO;
    }

    @Override
    public List<Permissions> toEntity(List<PermissionsDTO> Dto) {
        if (Dto == null) {
            return null;
        }
        return Dto.stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<PermissionsDTO> toDto(List<Permissions> entity) {
        return entity.stream().map(this::toDto).collect(Collectors.toList());
    }
}
