package ru.andreev.clothsshop.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.andreev.clothsshop.dto.ProductDTO;
import ru.andreev.clothsshop.exception.ProductNotFoundException;
import ru.andreev.clothsshop.model.Product;
import ru.andreev.clothsshop.repository.CategoryRepository;
import ru.andreev.clothsshop.repository.ColorRepository;
import ru.andreev.clothsshop.repository.ProductRepository;
import ru.andreev.clothsshop.repository.SizeRepository;
import ru.andreev.clothsshop.specification.ProductSpecification;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ColorRepository colorRepository, SizeRepository sizeRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
    }

    // Преобразование Product -> ProductDTO
    public ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory().getId(),
                product.getColors().stream().map(c -> c.getId()).collect(Collectors.toList()),
                product.getSizes().stream().map(s -> s.getId()).collect(Collectors.toList()),
                product.getPhotos()
        );
    }

    public Product convertToEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setCategory(categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ProductNotFoundException(productDTO.getCategoryId())));

        product.setColors(colorRepository.findAllById(productDTO.getColorIds()));
        product.setSizes(sizeRepository.findAllById(productDTO.getSizeIds()));
        product.setPhotos(productDTO.getPhotos());
        return product;
    }

    public ProductDTO getProductById(Long id) {
        return convertToDTO(productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id)));
    }

    public ProductDTO addProduct(ProductDTO productDTO) {
        return convertToDTO(productRepository.save(convertToEntity(productDTO)));
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setCategory(categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ProductNotFoundException(productDTO.getCategoryId())));
        product.setColors(colorRepository.findAllById(productDTO.getColorIds()));
        product.setSizes(sizeRepository.findAllById(productDTO.getSizeIds()));
        product.setPhotos(productDTO.getPhotos());

        return convertToDTO(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> searchProducts(String name, String color, String size, Double minPrice, Double maxPrice, String category) {
        Specification<Product> spec = Specification.where(ProductSpecification.hasName(name))
                .and(ProductSpecification.hasColor(color))
                .and(ProductSpecification.hasSize(size))
                .and(ProductSpecification.hasPriceBetween(minPrice, maxPrice))
                .and(ProductSpecification.hasCategory(category));

        List<Product> products = productRepository.findAll(spec);
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}