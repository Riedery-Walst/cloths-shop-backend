package ru.andreev.clothsshop.service;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.andreev.clothsshop.dto.ProductDTO;
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

    // Преобразование Product -> ProductDTO
    public ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getColors().stream().map(c -> c.getId()).collect(Collectors.toList()),
                product.getSizes().stream().map(s -> s.getId()).collect(Collectors.toList()),
                product.getPhotos().stream()
                        .map(ProductPhoto::getPhotoUrl)
                        .collect(Collectors.toList()).reversed()
        );
    }

    public Product convertToEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());

        product.setColors(colorRepository.findAllById(productDTO.getColorIds()));
        product.setSizes(sizeRepository.findAllById(productDTO.getSizeIds()));

        // Преобразуем URL-адреса из DTO в объекты ProductPhoto
        List<ProductPhoto> photos = productDTO.getPhotos().stream()
                .map(url -> {
                    ProductPhoto photo = new ProductPhoto();
                    photo.setPhotoUrl(url);
                    photo.setProduct(product); // Устанавливаем связь с продуктом
                    return photo;
                })
                .collect(Collectors.toList());

        product.setPhotos(photos); // Теперь это List<ProductPhoto>

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
        product.setColors(colorRepository.findAllById(productDTO.getColorIds()));
        product.setSizes(sizeRepository.findAllById(productDTO.getSizeIds()));

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