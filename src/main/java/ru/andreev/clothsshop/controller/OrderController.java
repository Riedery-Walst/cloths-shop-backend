package ru.andreev.clothsshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.dto.OrderDTO;
import ru.andreev.clothsshop.dto.UserDTO;
import ru.andreev.clothsshop.model.OrderStatus;
import ru.andreev.clothsshop.model.User;
import ru.andreev.clothsshop.service.OrderService;
import ru.andreev.clothsshop.service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;


    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    // Получение данных для заполнения формы оформления заказа
    @GetMapping("/checkout")
    public ResponseEntity<UserDTO> getCheckoutDetails(Principal principal) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            UserDTO userDTO = userService.convertToDTO(user);
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.ok(new UserDTO());
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @RequestBody OrderDTO orderDTO,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        OrderDTO createdOrder = orderService.createOrder(orderDTO, userEmail);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderDTO>> getMyOrders(Authentication authentication) {
        String userEmail = authentication.getName(); // Получаем email пользователя
        List<OrderDTO> orders = orderService.getOrdersByUser(userEmail);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }
}