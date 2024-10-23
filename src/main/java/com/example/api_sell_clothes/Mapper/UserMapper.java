package com.example.api_sell_clothes.Mapper;

import com.example.api_sell_clothes.DTO.UsersDTO;
import com.example.api_sell_clothes.Entity.Roles;
import com.example.api_sell_clothes.Entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper implements EntityMapper<Users, UsersDTO> {

    @Override
    public Users toEntity(UsersDTO dto) {
        if (dto == null) {
            return null;
        }
        Users users = Users.builder()
                .userId(dto.getUserId())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .enabled(dto.getEnabled())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
        if (dto.getRoles() != null) {
            users.setRoles(dto.getRoles().stream()
                    .map(roleName -> Roles.builder().roleName(roleName).build())
                    .collect(Collectors.toSet())
            );
        }
        return users;
    }

    @Override
    public UsersDTO toDto(Users entity) {
        if (entity == null) {
            return null;
        }
        UsersDTO usersDTO = UsersDTO.builder()
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .phone(entity.getPhone())
                .enabled(entity.getEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
        if (entity.getRoles() != null) {
            usersDTO.setRoles(entity.getRoles().stream()
                    .map(Roles::getRoleName)
                    .collect(Collectors.toSet())
            );
        }
        return usersDTO;
    }

    @Override
    public List<Users> toEntity(List<UsersDTO> DtoList) {
        if (DtoList == null) {
            return null;
        }
        return DtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsersDTO> toDto(List<Users> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
