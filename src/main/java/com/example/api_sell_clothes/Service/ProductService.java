package com.example.api_sell_clothes.Service;

import com.example.api_sell_clothes.DTO.ProductsDTO;
import com.example.api_sell_clothes.Entity.Categories;
import com.example.api_sell_clothes.Entity.Products;
import com.example.api_sell_clothes.Exception.Common.*;
import com.example.api_sell_clothes.Mapper.ProductMapper;
import com.example.api_sell_clothes.Repository.CategoryRepository;
import com.example.api_sell_clothes.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductsDTO createProduct(ProductsDTO productDTO) {
        log.info("Tạo sản phẩm mới : {}", productDTO);
        validateProductData(productDTO);

        // Kiểm tra tên sản phẩm đã tồn tại chưa
        if (productRepository.findByProductName(productDTO.getProductName()) != null) {
            throw new DuplicateResourceException("Sản phẩm", "tên", productDTO.getProductName());
        }

        // Kiểm tra và lấy category
        Categories category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Danh mục", "id", productDTO.getCategoryId()));

        // Set thời gian
        productDTO.setCreatedAt(LocalDateTime.now());
        productDTO.setUpdatedAt(LocalDateTime.now());

        // Chuyển đổi DTO thành entity và lưu
        Products product = productMapper.toEntity(productDTO);
        product.setCategory(category);

        return productMapper.toDto(productRepository.save(product));
    }

    public ProductsDTO updateProduct(Long id, ProductsDTO productDTO) {
        log.info("Cập nhật sản phẩm : {}", productDTO);

        Products existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sản phẩm", "id", id));

        validateProductData(productDTO);

        // Kiểm tra tên sản phẩm có bị trùng không
        Products productWithSameName = productRepository.findByProductName(productDTO.getProductName());
        if (productWithSameName != null && !productWithSameName.getProductId().equals(id)) {
            throw new DuplicateResourceException("Sản phẩm", "tên", productDTO.getProductName());
        }

        // Kiểm tra và lấy category mới nếu có thay đổi
        if (!existingProduct.getCategory().getCategoryId().equals(productDTO.getCategoryId())) {
            Categories newCategory = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Danh mục", "id", productDTO.getCategoryId()));
            existingProduct.setCategory(newCategory);
        }

        LocalDateTime createdAt = existingProduct.getCreatedAt();

        // Cập nhật thông tin
        existingProduct.setProductName(productDTO.getProductName());
        existingProduct.setProductDescription(productDTO.getProductDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStock(productDTO.getStock());
        existingProduct.setImageUrl(productDTO.getImageUrl());
        existingProduct.setCreatedAt(createdAt); // Giữ nguyên thời gian tạo
        existingProduct.setUpdatedAt(LocalDateTime.now());

        return productMapper.toDto(productRepository.save(existingProduct));
    }

    public void deleteProduct(Long id) {
        log.info("Xóa sản phẩm : {}", id);

        Products product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sản phẩm", "id", id));

        // Kiểm tra xem sản phẩm có đang được sử dụng không
        if (isProductInUse(id)) {
            throw new ResourceInUseException("Sản phẩm", "đang được sử dụng trong đơn hàng");
        }

        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public List<ProductsDTO> getAllProducts() {
        log.info("Lấy tất cả sản phẩm");
        return productMapper.toDto(productRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ProductsDTO getProductById(Long id) {
        return productMapper.toDto(productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sản phẩm", "id", id)));
    }

    @Transactional(readOnly = true)
    public List<ProductsDTO> getProductsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Danh mục", "id", categoryId);
        }
        return productMapper.toDto(productRepository.findByCategory_CategoryId(categoryId));
    }

    @Transactional(readOnly = true)
    public List<ProductsDTO> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new InvalidDataException("Từ khóa tìm kiếm không được để trống");
        }
        return productMapper.toDto(
                productRepository.findByProductNameContainingIgnoreCaseOrProductDescriptionContainingIgnoreCase(
                        keyword, keyword));
    }

    // Lọc sản phẩm theo khoảng giá
    @Transactional(readOnly = true)
    public List<ProductsDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null || maxPrice == null) {
            throw new InvalidDataException("Giá min và max không được null");
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new InvalidDataException("Giá min không được lớn hơn giá max");
        }
        return productMapper.toDto(productRepository.findByPriceBetween(minPrice, maxPrice));
    }

    /**
     * Lọc sản phẩm theo số lượng tồn kho tối thiểu
     * @param minStock Số lượng tồn kho tối thiểu
     * @return Danh sách sản phẩm có tồn kho >= minStock
     */
    @Transactional(readOnly = true)
    public List<ProductsDTO> getProductsByStock(Long minStock) {
        if (minStock == null) {
            throw new InvalidDataException("Số lượng tồn kho tối thiểu không được null");
        }
        return productMapper.toDto(productRepository.findByStockGreaterThanEqual(minStock));
    }

    // Các phương thức hỗ trợ
    private void validateProductData(ProductsDTO productDTO) {
        if (productDTO == null) {
            throw new InvalidDataException("Dữ liệu sản phẩm không được null");
        }
        if (productDTO.getProductName() == null || productDTO.getProductName().trim().isEmpty()) {
            throw new InvalidDataException("Tên sản phẩm không được để trống");
        }
        if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDataException("Giá sản phẩm phải lớn hơn 0");
        }
        if (productDTO.getStock() == null || productDTO.getStock() < 0) {
            throw new InvalidDataException("Số lượng tồn kho không được âm");
        }
        if (productDTO.getCategoryId() == null) {
            throw new InvalidDataException("ID danh mục không được null");
        }
    }

    private boolean isProductInUse(Long productId) {
        // Implement logic kiểm tra sản phẩm có trong đơn hàng
        return false;
    }
}