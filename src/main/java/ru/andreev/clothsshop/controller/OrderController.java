package ru.andreev.clothsshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.dto.OrderDTO;
import ru.andreev.clothsshop.dto.UserDTO;
import ru.andreev.clothsshop.model.Order;
import ru.andreev.clothsshop.model.OrderStatus;
import ru.andreev.clothsshop.model.User;
import ru.andreev.clothsshop.service.OrderService;
import ru.andreev.clothsshop.service.UserService;

import java.security.Principal;

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

    // Создание нового заказа с использованием DTO
    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        orderDTO.setUserId(user.getId());
        Order createdOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(orderService.convertToDTO(createdOrder));
    }

    // Получение информации о заказе
    @GetMapping("/{orderId}")
    public OrderDTO getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return orderService.convertToDTO(order);
    }

    // Обновление статуса заказа
    @PutMapping("/{orderId}/status")
    public OrderDTO updateOrderStatus(@PathVariable Long orderId,
                                      @RequestParam OrderStatus status) {
        Order order = orderService.updateOrderStatus(orderId, status);
        return orderService.convertToDTO(order);
    }
}