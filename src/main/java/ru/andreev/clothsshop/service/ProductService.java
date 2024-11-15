package ru.andreev.clothsshop.service;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.andreev.clothsshop.dto.ColorDTO;
import ru.andreev.clothsshop.dto.ProductDTO;
import ru.andreev.clothsshop.dto.SizeDTO;
import ru.andreev.clothsshop.exception.ProductNotFoundException;
import ru.andreev.clothsshop.model.Product;
import ru.andreev.clothsshop.model.ProductPhoto;
import ru.andreev.clothsshop.repository.ColorRepository;
import ru.andreev.clothsshop.repository.ProductRepository;
import ru.andreev.clothsshop.repository.SizeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final ProductPhotoService productPhotoService;

    public ProductService(ProductRepository productRepository, ColorRepository colorRepository, SizeRepository sizeRepository, ProductPhotoService productPhotoService) {
        this.productRepository = productRepository;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
        this.productPhotoService = productPhotoService;
    }

    // Преобразование Product -> ProductDTO с полными данными о цветах и размерах
    public ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                // Преобразование списка Color в ColorDTO
                product.getColors().stream()
                        .map(color -> new ColorDTO(color.getId(), color.getName(), color.getHex()))
                        .collect(Collectors.toList()),
                // Преобразование списка Size в SizeDTO
                product.getSizes().stream()
                        .map(size -> new SizeDTO(size.getId(), size.getName()))
                        .collect(Collectors.toList()),
                product.getPhotos().stream()
                        .map(ProductPhoto::getPhotoUrl)
                        .collect(Collectors.toList())
        );
    }

    // Преобразование ProductDTO -> Product
    public Product convertToEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());

        // Преобразование ColorDTO и SizeDTO обратно в Color и Size
        product.setColors(colorRepository.findAllById(
                productDTO.getColors().stream()
                        .map(ColorDTO::getId)
                        .collect(Collectors.toList())
        ));
        product.setSizes(sizeRepository.findAllById(
                productDTO.getSizes().stream()
                        .map(SizeDTO::getId)
                        .collect(Collectors.toList())
        ));

        List<ProductPhoto> photos = productDTO.getPhotos().stream()
                .map(url -> {
                    ProductPhoto photo = new ProductPhoto();
                    photo.setPhotoUrl(url);
                    photo.setProduct(product);
                    return photo;
                })
                .collect(Collectors.toList());

        product.setPhotos(photos);
        return product;
    }

    public ProductDTO getProductById(Long id) {
        return convertToDTO(productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id)));
    }

    public ProductDTO addProduct(ProductDTO productDTO, List<MultipartFile> photos) {
        Product product = convertToEntity(productDTO);
        productRepository.save(product);

        productPhotoService.savePhotos(photos, product);

        return convertToDTO(product);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO, List<MultipartFile> photos) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Обновляем данные продукта
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setColors(colorRepository.findAllById(
                productDTO.getColors().stream()
                        .map(ColorDTO::getId)
                        .collect(Collectors.toList())
        ));
        product.setSizes(sizeRepository.findAllById(
                productDTO.getSizes().stream()
                        .map(SizeDTO::getId)
                        .collect(Collectors.toList())
        ));

        // Удаление старых фотографий
        productPhotoService.deletePhotos(product.getPhotos());

        // Сохранение новых фотографий
        productPhotoService.savePhotos(photos, product);

        return convertToDTO(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Удаление всех фотографий продукта
        productPhotoService.deletePhotos(product.getPhotos());

        // Удаление самого продукта
        productRepository.delete(product);
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Product> getProducts(Specification<Product> spec) {
        return productRepository.findAll(spec);
    }
}
