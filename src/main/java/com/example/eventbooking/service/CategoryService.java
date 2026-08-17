package com.example.eventbooking.service;

import com.example.eventbooking.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryService {
    private static final Logger log = LogManager.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;
}
