package ru.andreev.clothsshop.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.andreev.clothsshop.dto.ColorDTO;
import ru.andreev.clothsshop.dto.ProductDTO;
import ru.andreev.clothsshop.dto.SizeDTO;
import ru.andreev.clothsshop.exception.ProductNotFoundException;
import ru.andreev.clothsshop.model.Color;
import ru.andreev.clothsshop.model.Product;
import ru.andreev.clothsshop.model.ProductPhoto;
import ru.andreev.clothsshop.model.Size;
import ru.andreev.clothsshop.repository.ColorRepository;
import ru.andreev.clothsshop.repository.ProductRepository;
import ru.andreev.clothsshop.repository.SizeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductPhotoService productPhotoService;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;

    public ProductService(ProductRepository productRepository,
                          ProductPhotoService productPhotoService,
                          ColorRepository colorRepository,
                          SizeRepository sizeRepository) {
        this.productRepository = productRepository;
        this.productPhotoService = productPhotoService;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
    }

    public ProductDTO addProduct(ProductDTO productDTO, List<MultipartFile> photos) {
        Product product = convertToEntity(productDTO);
        productRepository.save(product);

        if (photos != null && !photos.isEmpty()) {
            productPhotoService.savePhotos(photos, product);
        }

        return convertToDTO(product);
    }

    public ProductDTO updateProduct(ProductDTO productDTO, List<MultipartFile> photos) {
        if (productDTO == null) {
            throw new IllegalArgumentException("ProductDTO не может быть null");
        }

        Product product = productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new RuntimeException("Продукт с ID " + productDTO.getId() + " не найден"));

        // Обновление данных продукта
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());

        // Проверка цветов и размеров
        product.setColors(productDTO.getColors() != null ? convertColors(productDTO.getColors()) : new ArrayList<>());
        product.setSizes(productDTO.getSizes() != null ? convertSizes(productDTO.getSizes()) : new ArrayList<>());

        productRepository.save(product);

        if (photos != null && !photos.isEmpty()) {
            productPhotoService.savePhotos(photos, product);
        }

        return convertToDTO(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Удаление всех фотографий продукта
        productPhotoService.deletePhotos(product.getPhotos());

        // Удаление самого продукта
        productRepository.delete(product);
    }

    public ProductDTO getProductById(Long id) {
        return convertToDTO(productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id)));
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private Product convertToEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setColors(convertColors(productDTO.getColors()));
        product.setSizes(convertSizes(productDTO.getSizes()));
        return product;
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setColors(product.getColors().stream()
                .map(color -> ColorDTO.builder()
                        .id(color.getId())
                        .name(color.getName())
                        .hex(color.getHex())
                        .build())
                .collect(Collectors.toList()));
        productDTO.setSizes(product.getSizes().stream()
                .map(size -> new SizeDTO(size.getId(), size.getName()))
                .toList());
        productDTO.setPhotos(product.getPhotos() != null
                ? product.getPhotos().stream()
                .map(ProductPhoto::getPhotoUrl)
                .toList()
                : new ArrayList<>());
        return productDTO;
    }

    private List<Color> convertColors(List<ColorDTO> colorDTOs) {
        if (colorDTOs == null || colorDTOs.isEmpty()) {
            return new ArrayList<>();
        }
        return colorDTOs.stream()
                .map(colorDTO -> colorRepository.findById(colorDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Цвет с ID " + colorDTO.getId() + " не найден")))
                .collect(Collectors.toList());  // Ensure this returns a mutable list
    }

    private List<Size> convertSizes(List<SizeDTO> sizeDTOs) {
        if (sizeDTOs == null || sizeDTOs.isEmpty()) {
            return new ArrayList<>();
        }
        return sizeDTOs.stream()
                .map(sizeDTO -> sizeRepository.findById(sizeDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Размер с ID " + sizeDTO.getId() + " не найден")))
                .collect(Collectors.toList());  // Ensure this returns a mutable list
    }
}
