package ru.andreev.clothsshop.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.andreev.clothsshop.dto.OrderDTO;
import ru.andreev.clothsshop.dto.OrderItemDTO;
import ru.andreev.clothsshop.model.*;
import ru.andreev.clothsshop.repository.OrderRepository;
import ru.andreev.clothsshop.repository.ProductRepository;
import ru.andreev.clothsshop.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO, String userEmail) {
        // Получаем пользователя по email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setCreatedDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            if (itemDTO.getProductId() == null) {
                throw new IllegalArgumentException("Product ID must not be null");
            }

            if (itemDTO.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero for product ID: " + itemDTO.getProductId());
            }

            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setColorId(itemDTO.getColorId());
            item.setSizeId(itemDTO.getSizeId());

            order.addItem(item);
        }

        order.recalculateTotalPrice();
        Order savedOrder = orderRepository.save(order);

        return convertToDTO(savedOrder);
    }

    public List<OrderDTO> getOrdersByUser(String userEmail) {
        List<Order> orders = orderRepository.findByUserEmail(userEmail);
        return orders.stream().map(this::convertToDTO).toList();
    }

    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return convertToDTO(order);
    }

    public OrderDTO updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    public OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setCreatedDate(order.getCreatedDate());
        orderDTO.setStatus(order.getStatus().name());
        orderDTO.setItems(order.getItems().stream().map(this::convertItemToDTO).toList());
        return orderDTO;
    }

    private OrderItemDTO convertItemToDTO(OrderItem item) {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(item.getProduct().getId());
        itemDTO.setQuantity(item.getQuantity());
        itemDTO.setPrice(item.getSubtotal());
        itemDTO.setColorId(item.getColorId());
        itemDTO.setSizeId(item.getSizeId());
        return itemDTO;
    }
}