package com.example.api_sell_clothes.Controller;

import com.example.api_sell_clothes.DTO.CategoriesDTO;
import com.example.api_sell_clothes.Service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // APIs cho Category cha
    @GetMapping("/parent")
    public ResponseEntity<List<CategoriesDTO>> getAllParentCategories() {
        return ResponseEntity.ok(categoryService.getParentCategories());
    }

    @PostMapping("/parent")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<CategoriesDTO> createParentCategory(@Valid @RequestBody CategoriesDTO categoryDTO) {
        try {
            return new ResponseEntity<>(categoryService.createParentCategory(categoryDTO), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Không thể tạo danh mục cha: " + e.getMessage());
        }
    }

    @PutMapping("/parent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<CategoriesDTO> updateParentCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoriesDTO categoryDTO) {
        try {
            return ResponseEntity.ok(categoryService.updateParentCategory(id, categoryDTO));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Không thể cập nhật danh mục cha: " + e.getMessage());
        }
    }

    @DeleteMapping("/parent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteParentCategory(@PathVariable Long id) {
        categoryService.deleteParentCategory(id);
        return ResponseEntity.noContent().build();
    }

    // APIs cho Category con
    @GetMapping("/parent/{parentId}/children")
    public ResponseEntity<List<CategoriesDTO>> getChildCategories(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getChildCategories(parentId));
    }

    @PostMapping("/parent/{parentId}/child")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<CategoriesDTO> createChildCategory(
            @PathVariable Long parentId,
            @Valid @RequestBody CategoriesDTO categoryDTO) {
        try {
            return new ResponseEntity<>(
                    categoryService.createChildCategory(parentId, categoryDTO),
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Không thể tạo danh mục con: " + e.getMessage());
        }
    }

    @PutMapping("/parent/{parentId}/child/{childId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<CategoriesDTO> updateChildCategory(
            @PathVariable Long parentId,
            @PathVariable Long childId,
            @Valid @RequestBody CategoriesDTO categoryDTO) {
        try {
            // Đảm bảo parentId trong DTO khớp với parentId trong URL
            categoryDTO.setParentCategoryId(parentId);
            return ResponseEntity.ok(categoryService.updateChildCategory(parentId, childId, categoryDTO));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Không thể cập nhật danh mục con: " + e.getMessage());
        }
    }

    @DeleteMapping("/child/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteChildCategory(@PathVariable Long id) {
        categoryService.deleteChildCategory(id);
        return ResponseEntity.noContent().build();
    }

    // APIs chung cho cả Category
    @GetMapping
    public ResponseEntity<List<CategoriesDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriesDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CategoriesDTO>> searchCategories(@RequestParam String keyword) {
        try {
            return ResponseEntity.ok(categoryService.searchCategories(keyword));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi tìm kiếm: " + e.getMessage());
        }
    }
}