package com.example.eventbooking.service;

import com.example.eventbooking.dto.request.CreateCategoryRequest;
import com.example.eventbooking.entity.Category;
import com.example.eventbooking.exception.ResourceNotFoundException;
import com.example.eventbooking.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_savesAndReturnsCategoryResponse() {
        CreateCategoryRequest request = CreateCategoryRequest.builder().name("Music").build();

        Category saved = new Category();
        saved.setId(1L);
        saved.setName("Music");
        saved.setActive(true);
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        var result = categoryService.createCategory(request);

        assertEquals("Music", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void deactivateCategory_throwsResourceNotFoundExceptionWhenMissing() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deactivateCategory(99L));
    }
}