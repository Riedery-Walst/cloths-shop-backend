package ru.andreev.clothsshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.dto.ProductDTO;
import ru.andreev.clothsshop.model.Category;
import ru.andreev.clothsshop.model.Color;
import ru.andreev.clothsshop.model.Size;
import ru.andreev.clothsshop.service.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    private final ProductService productService;
    private final SizeService sizeService;
    private final CategoryService categoryService;
    private final ColorService colorService;

    public AdminController(UserService userService, ProductService productService, SizeService sizeService, CategoryService categoryService, ColorService colorService) {
        this.userService = userService;
        this.productService = productService;
        this.sizeService = sizeService;
        this.categoryService = categoryService;
        this.colorService = colorService;
    }

    // Промоция пользователя в администратора
    @PostMapping("/users/promote")
    public ResponseEntity<?> promoteUser(@RequestParam String email) {
        return ResponseEntity.ok(userService.makeAdmin(email));
    }

    // Получить категорию по ID
    @GetMapping("/categories/{id}") // Измененный путь
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    // Добавить новую категорию
    @PostMapping("/categories") // Измененный путь
    public Category addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    // Обновить категорию
    @PutMapping("/categories/{id}") // Измененный путь
    public Category updateCategory(@PathVariable Long id, @RequestBody Category updatedCategory) {
        return categoryService.updateCategory(id, updatedCategory);
    }

    // Удалить категорию
    @DeleteMapping("/categories/{id}") // Измененный путь
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    // Добавить новый цвет
    @PostMapping("/colors") // Измененный путь
    public Color addColor(@RequestBody Color color) {
        return colorService.addColor(color);
    }

    // Обновить цвет
    @PutMapping("/colors/{id}") // Измененный путь
    public Color updateColor(@PathVariable Long id, @RequestBody Color updatedColor) {
        return colorService.updateColor(id, updatedColor);
    }

    // Удалить цвет
    @DeleteMapping("/colors/{id}") // Измененный путь
    public void deleteColor(@PathVariable Long id) {
        colorService.deleteColor(id);
    }

    // Добавить новый размер
    @PostMapping("/sizes") // Измененный путь
    public Size addSize(@RequestBody Size size) {
        return sizeService.addSize(size);
    }

    // Обновить размер
    @PutMapping("/sizes/{id}") // Измененный путь
    public Size updateSize(@PathVariable Long id, @RequestBody Size updatedSize) {
        return sizeService.updateSize(id, updatedSize);
    }

    // Удалить размер
    @DeleteMapping("/sizes/{id}") // Измененный путь
    public void deleteSize(@PathVariable Long id) {
        sizeService.deleteSize(id);
    }

    // Добавить новый продукт
    @PostMapping("/products") // Измененный путь
    public ProductDTO addProduct(@RequestBody ProductDTO productDTO) {
        return productService.addProduct(productDTO);
    }

    // Обновить продукт
    @PutMapping("/products/{id}") // Измененный путь
    public ProductDTO updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        return productService.updateProduct(id, productDTO);
    }

    // Удалить продукт
    @DeleteMapping("/products/{id}") // Измененный путь
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}