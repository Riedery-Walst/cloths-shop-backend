package ru.andreev.clothsshop.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.dto.ProductDTO;
import ru.andreev.clothsshop.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Получить все товары
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    // Получить товар по ID
    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // Добавить новый товар (доступно только для администраторов)
    @PostMapping("/admin/add")
    public ProductDTO addProduct(@Valid @RequestBody ProductDTO productDTO) {
        return productService.addProduct(productDTO);
    }

    // Обновить товар (доступно только для администраторов)
    @PutMapping("/admin/update/{id}")
    public ProductDTO updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        return productService.updateProduct(id, productDTO);
    }

    // Удалить товар (доступно только для администраторов)
    @DeleteMapping("/admin/delete/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}