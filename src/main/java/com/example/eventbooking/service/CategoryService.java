package com.example.eventbooking.service;

import com.example.eventbooking.dto.CategoryDTO;
import com.example.eventbooking.entity.Category;
import com.example.eventbooking.exception.ResourceNotFoundException;
import com.example.eventbooking.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {
    private static final Logger log = LogManager.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;

    private CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .active(category.isActive())
                .build();
    }

    private Category findCategoryOrThrow(Long id) {
        log.trace("Entering findCategoryOrThrow() — id={}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found — id={}", id);
                    return new ResourceNotFoundException("Category not found with id " + id);
                });
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        return toDTO(findCategoryOrThrow(id));
    }

    public CategoryDTO createCategory(CategoryDTO dto) {
        log.trace("Entering createCategory() — name={}", dto.getName());
        Category category = new Category();
        category.setName(dto.getName());
        Category saved = categoryRepository.save(category);
        log.info("Category created — id={}, name='{}'", saved.getId(), saved.getName());
        return toDTO(saved);
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        log.trace("Entering updateCategory() — id={}", id);
        Category existing = findCategoryOrThrow(id);
        existing.setName(dto.getName());
        Category saved = categoryRepository.save(existing);
        log.info("Category updated — id={}", saved.getId());
        return toDTO(saved);
    }

    public void deactivateCategory(Long id) {
        log.trace("Entering deactivateCategory() — id={}", id);
        Category category = findCategoryOrThrow(id);
        category.setActive(false);
        categoryRepository.save(category);
        log.info("Category deactivated — id={}", id);
    }
}