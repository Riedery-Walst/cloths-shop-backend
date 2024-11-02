package ru.andreev.clothsshop.controller;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.dto.ProductDTO;
import ru.andreev.clothsshop.model.Product;
import ru.andreev.clothsshop.service.ProductService;
import ru.andreev.clothsshop.specification.ProductSpecification;

import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping("/filter")
    public List<ProductDTO> getFilteredProducts(@RequestBody ProductDTO filter) {
        Specification<Product> spec = Specification.where(
                        ProductSpecification.hasCategoryId(filter.getCategoryId()))
                .and(ProductSpecification.hasColorIds(filter.getColorIds()))
                .and(ProductSpecification.hasSizeIds(filter.getSizeIds()));

        List<Product> products = productService.getProducts(spec);

        return products.stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }

    private ProductDTO convertToProductDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory().getId(),
                product.getColors().stream().map(color -> color.getId()).collect(Collectors.toList()),
                product.getSizes().stream().map(size -> size.getId()).collect(Collectors.toList()),
                product.getPhotos()
        );
    }
}