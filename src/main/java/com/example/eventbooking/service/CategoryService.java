package com.example.eventbooking.service;

import com.example.eventbooking.dto.request.CreateCategoryRequest;
import com.example.eventbooking.dto.response.CategoryResponse;
import com.example.eventbooking.entity.Category;
import com.example.eventbooking.exception.DuplicateResourceException;
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

    private CategoryResponse toDTO(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .active(category.isActive())
                .build();
    }

    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.trace("Entering createCategory() — name={}", request.getName());
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category '" + request.getName() + "' already exists");
        }
        Category category = new Category();
        category.setName(request.getName());
        Category saved = categoryRepository.save(category);
        log.info("Category created — id={}, name='{}'", saved.getId(), saved.getName());
        return toDTO(saved);
    }

    private Category findCategoryOrThrow(Long id) {
        log.trace("Entering findCategoryOrThrow() — id={}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found — id={}", id);
                    return new ResourceNotFoundException("Category not found with id " + id);
                });
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        return toDTO(findCategoryOrThrow(id));
    }

    public CategoryResponse updateCategory(Long id, CreateCategoryRequest request) {
        log.trace("Entering updateCategory() — id={}", id);
        Category existing = findCategoryOrThrow(id);
        existing.setName(request.getName());
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