package ru.andreev.clothsshop.controller;

import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.model.Category;
import ru.andreev.clothsshop.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Получить все категории
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // Получить категорию по ID
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    // Добавить новую категорию (доступно только для администраторов)
    @PostMapping("/admin/add")
    public Category addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    // Обновить категорию (доступно только для администраторов)
    @PutMapping("/admin/update/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category updatedCategory) {
        return categoryService.updateCategory(id, updatedCategory);
    }

    // Удалить категорию (доступно только для администраторов)
    @DeleteMapping("/admin/delete/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}