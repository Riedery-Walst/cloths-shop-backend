package ru.andreev.clothsshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.andreev.clothsshop.dto.ColorDTO;
import ru.andreev.clothsshop.dto.ProductDTO;
import ru.andreev.clothsshop.dto.SizeDTO;
import ru.andreev.clothsshop.model.Category;
import ru.andreev.clothsshop.service.*;

import java.util.List;

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

    @PostMapping("/colors")
    public ResponseEntity<ColorDTO> createColor(@RequestBody ColorDTO colorDTO) {
        ColorDTO newColor = colorService.createColor(colorDTO);
        return ResponseEntity.ok(newColor);
    }

    // Обновить цвет по ID
    @PutMapping("/colors/{id}")
    public ResponseEntity<ColorDTO> updateColor(@PathVariable Long id, @RequestBody ColorDTO colorDTO) {
        ColorDTO updatedColor = colorService.updateColor(id, colorDTO);
        return ResponseEntity.ok(updatedColor);
    }

    // Удалить цвет по ID
    @DeleteMapping("/colors/{id}")
    public ResponseEntity<Void> deleteColor(@PathVariable Long id) {
        colorService.deleteColor(id);
        return ResponseEntity.noContent().build();
    }

    // Добавить новый размер
    @PostMapping("/sizes")
    public ResponseEntity<SizeDTO> addSize(@RequestBody SizeDTO sizeDTO) {
        SizeDTO newSize = sizeService.addSize(sizeDTO);
        return ResponseEntity.ok(newSize);
    }

    // Обновить размер по ID
    @PutMapping("/sizes/{id}")
    public ResponseEntity<SizeDTO> updateSize(@PathVariable Long id, @RequestBody SizeDTO updatedSizeDTO) {
        SizeDTO updatedSize = sizeService.updateSize(id, updatedSizeDTO);
        return ResponseEntity.ok(updatedSize);
    }

    // Удалить размер по ID
    @DeleteMapping("/sizes/{id}")
    public ResponseEntity<Void> deleteSize(@PathVariable Long id) {
        sizeService.deleteSize(id);
        return ResponseEntity.noContent().build();
    }

    // Добавить новый продукт
    @PostMapping("/products")
    public ProductDTO addProduct(@RequestPart("product") ProductDTO productDTO,
                                 @RequestPart("photos") List<MultipartFile> photos) {
        return productService.addProduct(productDTO, photos);
    }

    // Обновить продукт
    @PutMapping("/products/{id}")
    public ProductDTO updateProduct(@PathVariable Long id,
                                    @RequestPart("product") ProductDTO productDTO,
                                    @RequestPart("photos") List<MultipartFile> photos) {
        return productService.updateProduct(id, productDTO, photos);
    }

    // Удалить продукт
    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}