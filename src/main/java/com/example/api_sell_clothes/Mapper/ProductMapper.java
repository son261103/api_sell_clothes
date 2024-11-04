package com.example.api_sell_clothes.Mapper;

import com.example.api_sell_clothes.DTO.ProductsDTO;
import com.example.api_sell_clothes.Entity.Products;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper implements EntityMapper<Products, ProductsDTO> {

    @Override
    public Products toEntity(ProductsDTO dto) {
        if (dto == null) {
            return null;
        }

        return Products.builder()
                .productId(dto.getProductId())
                .productName(dto.getProductName())
                .productDescription(dto.getProductDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                // Lưu ý: Cần set category riêng vì DTO chỉ chứa categoryId
                .imageUrl(dto.getImageUrl())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    @Override
    public ProductsDTO toDto(Products entity) {
        if (entity == null) {
            return null;
        }

        return ProductsDTO.builder()
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .productDescription(entity.getProductDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getCategoryId() : null)
                .imageUrl(entity.getImageUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<Products> toEntity(List<ProductsDTO> dtoList) {
        if (dtoList == null) {
            return new ArrayList<>();
        }

        return dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductsDTO> toDto(List<Products> entityList) {
        if (entityList == null) {
            return new ArrayList<>();
        }

        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}