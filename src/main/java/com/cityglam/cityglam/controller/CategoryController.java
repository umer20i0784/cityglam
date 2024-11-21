package com.cityglam.cityglam.controller;
import com.cityglam.cityglam.entity.*;
import com.cityglam.cityglam.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cityglam.cityglam.entity.Country;
import com.cityglam.cityglam.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.*;
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    // Fetch all categories
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }

    // Add new category
    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody Category newCategory) {
        Category savedCategory = categoryRepository.save(newCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    // Edit category
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category updatedCategory) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(updatedCategory.getName());
                    category.setStatus(updatedCategory.getStatus());
                    category.setLogoUrl(updatedCategory.getLogoUrl());
                    Category savedCategory = categoryRepository.save(category);
                    return ResponseEntity.ok(savedCategory);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<Object> suspendCategory(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setStatus("SUSPENDED");
                    categoryRepository.save(category);
                    return ResponseEntity.<Void>ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Delete category (check dependencies)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (hasDependencies(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private boolean hasDependencies(Long categoryId) {
        // TODO:  check if businesses are using this category
        return false;
    }
}

