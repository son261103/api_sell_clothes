package com.example.api_sell_clothes.Service;

import com.example.api_sell_clothes.DTO.CategoriesDTO;
import com.example.api_sell_clothes.Entity.Categories;
import com.example.api_sell_clothes.Exception.Common.*;
import com.example.api_sell_clothes.Mapper.CategoryMapper;
import com.example.api_sell_clothes.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    // CRUD cho Category cha
    public CategoriesDTO createParentCategory(CategoriesDTO categoryDTO) {
        log.info("Tạo danh mục cha mới : {}", categoryDTO);

        if (categoryDTO.getParentCategoryId() != null) {
            throw new InvalidDataException("Danh mục cha không được có parent id");
        }
        categoryDTO.setParentCategoryId(null);
        return createCategory(categoryDTO);
    }

    public CategoriesDTO updateParentCategory(Long id, CategoriesDTO categoryDTO) {
        log.info("Cập nhật danh mục cha : {}", categoryDTO);

        Categories category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Danh mục cha", "id", id));

        if (category.getParentCategoryId() != null) {
            throw new InvalidDataException("ID không phải của danh mục cha");
        }

        categoryDTO.setParentCategoryId(null);
        return updateCategory(id, categoryDTO);
    }

    public void deleteParentCategory(Long id) {
        log.info("Xóa danh mục cha : {}", id);

        Categories category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Danh mục cha", "id", id));

        if (category.getParentCategoryId() != null) {
            throw new InvalidDataException("ID không phải của danh mục cha");
        }

        // Lấy và xóa tất cả danh mục con
        List<Categories> children = categoryRepository.findByParentCategoryId(id);
        if (!children.isEmpty()) {
            categoryRepository.deleteAll(children);
            log.info("Đã xóa {} danh mục con của danh mục cha : {}", children.size(), id);
        }

        // Xóa danh mục cha
        categoryRepository.delete(category);
    }


    // CRUD cho Category con
    public CategoriesDTO createChildCategory(Long parentId, CategoriesDTO categoryDTO) {
        log.info("Tạo danh mục con mới cho parent {} : {}", parentId, categoryDTO);

        Categories parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Danh mục cha", "id", parentId));

        if (parent.getParentCategoryId() != null) {
            throw new InvalidDataException("Không thể tạo danh mục con cho danh mục không phải cha");
        }

        categoryDTO.setParentCategoryId(parentId);
        return createCategory(categoryDTO);
    }

    public CategoriesDTO updateChildCategory(Long parentId, Long childId, CategoriesDTO categoryDTO) {
        log.info("Cập nhật danh mục con : {}", categoryDTO);

        // Kiểm tra danh mục con tồn tại
        Categories child = categoryRepository.findById(childId)
                .orElseThrow(() -> new NotFoundException("Danh mục con", "id", childId));

        // Kiểm tra danh mục cha tồn tại
        Categories parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Danh mục cha", "id", parentId));

        // Kiểm tra xem childId có phải là danh mục con không
        if (child.getParentCategoryId() == null) {
            throw new InvalidDataException("ID " + childId + " không phải của danh mục con");
        }

        // Kiểm tra xem parentId có matches với parent của child category không
        if (!child.getParentCategoryId().equals(parentId)) {
            throw new InvalidDataException("Danh mục con " + childId + " không thuộc danh mục cha " + parentId);
        }

        // Kiểm tra xem parentId có phải là danh mục cha (không có parent)
        if (parent.getParentCategoryId() != null) {
            throw new InvalidDataException("ID " + parentId + " không phải của danh mục cha");
        }

        return updateCategory(childId, categoryDTO);
    }

    public void deleteChildCategory(Long id) {
        log.info("Xóa danh mục con : {}", id);

        Categories child = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Danh mục con", "id", id));

        if (child.getParentCategoryId() == null) {
            throw new InvalidDataException("ID không phải của danh mục con");
        }

        if (isCategoryInUse(id)) {
            throw new ResourceInUseException("Danh mục con", "đang được sử dụng bởi sản phẩm");
        }

        categoryRepository.delete(child);
    }

    // Các phương thức CRUD cơ bản
    public CategoriesDTO createCategory(CategoriesDTO categoryDTO) {
        validateCategoryData(categoryDTO);

        if (categoryRepository.findByCategoryName(categoryDTO.getCategoryName()) != null) {
            throw new DuplicateResourceException("Danh mục", "tên", categoryDTO.getCategoryName());
        }

        categoryDTO.setCreatedAt(LocalDateTime.now());
        categoryDTO.setUpdatedAt(LocalDateTime.now());

        Categories category = categoryMapper.toEntity(categoryDTO);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    public CategoriesDTO updateCategory(Long id, CategoriesDTO categoryDTO) {
        Categories existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Danh mục", "id", id));

        validateCategoryData(categoryDTO);

        Categories categoryWithSameName = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if (categoryWithSameName != null && !categoryWithSameName.getCategoryId().equals(id)) {
            throw new DuplicateResourceException("Danh mục", "tên", categoryDTO.getCategoryName());
        }

        existingCategory.setCategoryName(categoryDTO.getCategoryName());
        existingCategory.setCategoryDescription(categoryDTO.getCategoryDescription());
        existingCategory.setUpdatedAt(LocalDateTime.now());

        return categoryMapper.toDto(categoryRepository.save(existingCategory));
    }

    @Transactional(readOnly = true)
    public List<CategoriesDTO> getAllCategories() {
        log.info("Lấy tất cả danh mục");
        List<Categories> categories = categoryRepository.findAll();
        return categoryMapper.toDto(categories);
    }

    @Transactional(readOnly = true)
    public CategoriesDTO getCategoryById(Long id) {
        return categoryMapper.toDto(categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Danh mục", "id", id)));
    }

    // Các phương thức truy vấn bổ sung
    @Transactional(readOnly = true)
    public List<CategoriesDTO> getParentCategories() {
        return categoryMapper.toDto(categoryRepository.findByParentCategoryIdIsNull());
    }

    @Transactional(readOnly = true)
    public List<CategoriesDTO> getChildCategories(Long parentId) {
        validateParentCategory(parentId);
        return categoryMapper.toDto(categoryRepository.findByParentCategoryId(parentId));
    }

    @Transactional(readOnly = true)
    public List<CategoriesDTO> searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new InvalidDataException("Từ khóa tìm kiếm không được để trống");
        }
        return categoryMapper.toDto(categoryRepository.findByCategoryNameContainingIgnoreCase(keyword));
    }

    // Các phương thức hỗ trợ
    private void validateCategoryData(CategoriesDTO categoryDTO) {
        if (categoryDTO == null) {
            throw new InvalidDataException("Dữ liệu danh mục không được null");
        }
        if (categoryDTO.getCategoryName() == null || categoryDTO.getCategoryName().trim().isEmpty()) {
            throw new InvalidDataException("Tên danh mục không được để trống");
        }
    }

    private void validateParentCategory(Long parentId) {
        if (!categoryRepository.existsById(parentId)) {
            throw new NotFoundException("Danh mục cha", "id", parentId);
        }
        Categories parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Danh mục cha", "id", parentId));
        if (parent.getParentCategoryId() != null) {
            throw new InvalidDataException("ID không phải của danh mục cha");
        }
    }

    private boolean isCategoryInUse(Long categoryId) {
        // Implement logic kiểm tra sản phẩm
        return false;
    }
}