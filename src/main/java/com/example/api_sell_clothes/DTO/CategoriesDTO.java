package com.example.api_sell_clothes.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriesDTO {
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private Long parentCategoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
