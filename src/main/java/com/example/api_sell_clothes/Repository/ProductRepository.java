package com.example.api_sell_clothes.Repository;

import com.example.api_sell_clothes.Entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Products, Long> {
    Products findByProductName(String productName);
    List<Products> findByCategory_CategoryId(Long categoryId);
    List<Products> findByProductNameContainingIgnoreCaseOrProductDescriptionContainingIgnoreCase(
            String name, String description);
    List<Products> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Products> findByStockGreaterThanEqual(Long minStock);
}