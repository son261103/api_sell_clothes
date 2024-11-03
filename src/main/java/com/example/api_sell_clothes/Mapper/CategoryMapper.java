package com.example.api_sell_clothes.Mapper;

import com.example.api_sell_clothes.DTO.CategoriesDTO;
import com.example.api_sell_clothes.Entity.Categories;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper implements EntityMapper<Categories, CategoriesDTO> {
    @Override
    public Categories toEntity(CategoriesDTO dto) {
        if (dto == null) {
            return null;
        }
        Categories categories = Categories.builder()
                .categoryId(dto.getCategoryId())
                .categoryName(dto.getCategoryName())
                .categoryDescription(dto.getCategoryDescription())
                .parentCategoryId(dto.getParentCategoryId())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
        return categories;
    }

    @Override
    public CategoriesDTO toDto(Categories entity) {
        if (entity == null) {
            return null;
        }
        CategoriesDTO categoriesDTO = CategoriesDTO.builder()
                .categoryId(entity.getCategoryId())
                .categoryName(entity.getCategoryName())
                .categoryDescription(entity.getCategoryDescription())
                .parentCategoryId(entity.getParentCategoryId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
        return categoriesDTO;
    }

    @Override
    public List<Categories> toEntity(List<CategoriesDTO> Dto) {
        if (Dto == null) {
            return null;
        }
        return Dto.stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<CategoriesDTO> toDto(List<Categories> entity) {
        if (entity == null) {
            return null;
        }
        return entity.stream().map(this::toDto).collect(Collectors.toList());
    }
}
