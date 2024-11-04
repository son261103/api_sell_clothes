package com.example.api_sell_clothes.Controller;

import com.example.api_sell_clothes.DTO.ProductsDTO;
import com.example.api_sell_clothes.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ProductsDTO> createProduct(@Valid @RequestBody ProductsDTO productDTO) {
        try {
            return new ResponseEntity<>(productService.createProduct(productDTO), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Không thể tạo sản phẩm: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ProductsDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductsDTO productDTO) {
        try {
            return ResponseEntity.ok(productService.updateProduct(id, productDTO));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Không thể cập nhật sản phẩm: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductsDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductsDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductsDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        try {
            return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi khi lấy sản phẩm theo danh mục: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductsDTO>> searchProducts(@RequestParam String keyword) {
        try {
            return ResponseEntity.ok(productService.searchProducts(keyword));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi tìm kiếm: " + e.getMessage());
        }
    }

    @GetMapping("/filter/price")
    public ResponseEntity<List<ProductsDTO>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        try {
            return ResponseEntity.ok(productService.getProductsByPriceRange(minPrice, maxPrice));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi khi lọc theo giá: " + e.getMessage());
        }
    }

    @GetMapping("/filter/stock")
    public ResponseEntity<List<ProductsDTO>> getProductsByMinStock(@RequestParam Long minStock) {
        try {
            return ResponseEntity.ok(productService.getProductsByStock(minStock));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi khi lọc theo tồn kho: " + e.getMessage());
        }
    }
}