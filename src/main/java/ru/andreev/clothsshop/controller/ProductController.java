package ru.andreev.clothsshop.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.dto.ProductDTO;
import ru.andreev.clothsshop.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

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

    @GetMapping("/search")
    public List<ProductDTO> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String category) {

        return productService.searchProducts(name, color, size, minPrice, maxPrice, category);
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