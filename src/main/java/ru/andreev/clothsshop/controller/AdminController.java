package ru.andreev.clothsshop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.andreev.clothsshop.dto.ColorDTO;
import ru.andreev.clothsshop.dto.OrderDTO;
import ru.andreev.clothsshop.dto.ProductDTO;
import ru.andreev.clothsshop.dto.SizeDTO;
import ru.andreev.clothsshop.model.Payment;
import ru.andreev.clothsshop.service.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    private final ProductService productService;
    private final SizeService sizeService;
    private final ColorService colorService;
    private final PaymentService paymentService;
    private final OrderService orderService;

    public AdminController(UserService userService, ProductService productService, SizeService sizeService, ColorService colorService, PaymentService paymentService, OrderService orderService) {
        this.userService = userService;
        this.productService = productService;
        this.sizeService = sizeService;
        this.colorService = colorService;
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    // Промоция пользователя в администратора
    @PutMapping("/users/promote")
    public ResponseEntity<?> promoteUser(@RequestParam String email) {
        return ResponseEntity.ok(userService.makeAdmin(email));
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

    @PostMapping(value = "/products", consumes = {"multipart/form-data"})
    public ProductDTO addProduct(
            @RequestPart("product") String productJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
        ProductDTO productDTO = convertJsonToProductDTO(productJson);

        return productService.addProduct(productDTO, photos);
    }

    @PutMapping(value = "products/{id}", consumes = {"multipart/form-data"})
    public ProductDTO updateProduct(
            @PathVariable Long id,
            @RequestPart("product") String productJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
        if (productJson == null || productJson.isEmpty()) {
            throw new IllegalArgumentException("Product data should not be empty");
        }
        ProductDTO productDTO = convertJsonToProductDTO(productJson);
        productDTO.setId(id);

        return productService.updateProduct(productDTO, photos);
    }

    // Удалить продукт
    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    // Метод для преобразования JSON в ProductDTO
    private ProductDTO convertJsonToProductDTO(String productJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(productJson, ProductDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid format JSON for product", e);
        }
    }

    // Получение платежа
    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long paymentId) {
        Optional<Payment> payment = paymentService.getPaymentById(paymentId);
        return payment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Получение всех платежей
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderDTO> orders = orderService.getAllOrders(page, size);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<OrderDTO>> getOrdersByUser(
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderDTO> orders = orderService.getOrdersByUser(userEmail, page, size);
        return ResponseEntity.ok(orders);
    }
}