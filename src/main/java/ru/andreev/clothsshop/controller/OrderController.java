package ru.andreev.clothsshop.controller;

import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.dto.OrderDTO;
import ru.andreev.clothsshop.model.Order;
import ru.andreev.clothsshop.model.OrderStatus;
import ru.andreev.clothsshop.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Создание нового заказа с использованием DTO
    @PostMapping("/create")
    public OrderDTO createOrder(@RequestBody OrderDTO orderDTO) {
        Order createdOrder = orderService.createOrder(orderDTO);
        return orderService.convertToDTO(createdOrder);
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