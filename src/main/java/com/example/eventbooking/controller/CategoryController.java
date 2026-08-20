package com.example.eventbooking.controller;

import com.example.eventbooking.dto.request.CreateCategoryRequest;
import com.example.eventbooking.dto.response.CategoryResponse;
import com.example.eventbooking.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateCategory(@PathVariable Long id) {
        categoryService.deactivateCategory(id);
        return ResponseEntity.noContent().build();
    }
}