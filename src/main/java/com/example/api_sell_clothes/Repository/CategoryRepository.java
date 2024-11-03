package com.example.api_sell_clothes.Repository;

import com.example.api_sell_clothes.Entity.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Categories, Long> {
    /**
     * Tìm danh mục theo tên chính xác
     */
    Categories findByCategoryName(String name);

    /**
     * Tìm tất cả danh mục con của một danh mục cha
     */
    List<Categories> findByParentCategoryId(Long parentId);

    /**
     * Tìm tất cả danh mục gốc (không có danh mục cha)
     */
    List<Categories> findByParentCategoryIdIsNull();

    /**
     * Tìm kiếm danh mục theo tên (không phân biệt hoa thường)
     */
    List<Categories> findByCategoryNameContainingIgnoreCase(String keyword);

    /**
     * Kiểm tra danh mục tồn tại theo ID
     */
    boolean existsById(Long id);

    /**
     * Lấy danh sách danh mục có phân trang
     */
    Page<Categories> findAll(Pageable pageable);
}