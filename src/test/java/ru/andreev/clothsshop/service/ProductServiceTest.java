package ru.andreev.clothsshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import ru.andreev.clothsshop.dto.ColorDTO;
import ru.andreev.clothsshop.dto.ProductDTO;
import ru.andreev.clothsshop.dto.SizeDTO;
import ru.andreev.clothsshop.model.Color;
import ru.andreev.clothsshop.model.Product;
import ru.andreev.clothsshop.model.Size;
import ru.andreev.clothsshop.repository.ColorRepository;
import ru.andreev.clothsshop.repository.ProductRepository;
import ru.andreev.clothsshop.repository.SizeRepository;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductPhotoService productPhotoService;

    @Mock
    private ColorRepository colorRepository;

    @Mock
    private SizeRepository sizeRepository;

    @InjectMocks
    private ProductService productService;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Product 1");
        productDTO.setDescription("Description 1");
        productDTO.setPrice(100.0);
        productDTO.setQuantity(10);
        productDTO.setColors(Arrays.asList(new ColorDTO(1L, "Red", "#FF0000")));
        productDTO.setSizes(Arrays.asList(new SizeDTO(1L, "M")));
    }

    @Test
    void testAddProduct() {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());

        Color color = new Color();
        color.setId(1L);
        color.setName("Red");
        color.setHex("#FF0000");

        Size size = new Size();
        size.setId(1L);
        size.setName("M");

        product.setColors(Arrays.asList(color));
        product.setSizes(Arrays.asList(size));

        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(colorRepository.findById(1L)).thenReturn(java.util.Optional.of(color));
        when(sizeRepository.findById(1L)).thenReturn(java.util.Optional.of(size));

        List<MultipartFile> photos = mock(List.class);
        productService.addProduct(productDTO, photos);

        verify(productRepository, times(1)).save(any(Product.class));
        verify(productPhotoService, times(1)).savePhotos(any(), any(Product.class));
    }

    @Test
    void testUpdateProduct() {
        ProductDTO updatedProductDTO = new ProductDTO();
        updatedProductDTO.setId(1L);
        updatedProductDTO.setName("Updated Product");
        updatedProductDTO.setDescription("Updated Description");
        updatedProductDTO.setPrice(150.0);
        updatedProductDTO.setQuantity(20);
        updatedProductDTO.setColors(Arrays.asList(new ColorDTO(1L, "Blue", "#0000FF")));
        updatedProductDTO.setSizes(Arrays.asList(new SizeDTO(1L, "L")));

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Old Product");

        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(existingProduct));
        when(colorRepository.findById(1L)).thenReturn(java.util.Optional.of(new Color()));
        when(sizeRepository.findById(1L)).thenReturn(java.util.Optional.of(new Size()));

        productService.updateProduct(updatedProductDTO, null);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(product);
    }
}
