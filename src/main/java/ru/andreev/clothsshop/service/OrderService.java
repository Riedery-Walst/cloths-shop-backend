package ru.andreev.clothsshop.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.andreev.clothsshop.dto.AddressDTO;
import ru.andreev.clothsshop.dto.OrderDTO;
import ru.andreev.clothsshop.dto.OrderItemDTO;
import ru.andreev.clothsshop.dto.UserDTO;
import ru.andreev.clothsshop.model.*;
import ru.andreev.clothsshop.repository.CartItemRepository;
import ru.andreev.clothsshop.repository.OrderRepository;
import ru.andreev.clothsshop.repository.ProductRepository;
import ru.andreev.clothsshop.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository, CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO, String userEmail) {
        // Получаем пользователя из репозитория
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // Создаем новый объект заказа
        Order order = new Order();
        order.setUser(user);
        order.setCreatedDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        // Обрабатываем каждый товар в заказе
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            if (itemDTO.getProductId() == null) {
                throw new IllegalArgumentException("ID товара не может быть null");
            }

            if (itemDTO.getQuantity() <= 0) {
                throw new IllegalArgumentException(
                        "Количество должно быть больше нуля для товара с ID: " + itemDTO.getProductId());
            }

            // Получаем товар из репозитория
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Товар не найден"));

            // Обновляем количество товара в репозитории
            product.setQuantity(product.getQuantity() - itemDTO.getQuantity());
            productRepository.save(product);

            // Создаем и добавляем элемент заказа
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setColorId(itemDTO.getColorId());
            item.setSizeId(itemDTO.getSizeId());

            // Очищаем корзину пользователя после создания заказа
            clearUserCart(user);

            order.addItem(item);
        }

        // Перерасчет общей стоимости заказа
        order.recalculateTotalPrice();

        // Сохраняем заказ и возвращаем DTO
        Order savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    private void clearUserCart(User user) {
        List<CartItem> cartItems = cartItemRepository.findByCartUserId(user.getId());
        if (!cartItems.isEmpty()) {
            cartItemRepository.deleteAll(cartItems);  // Удаляем все элементы из корзины
            log.info("Корзина очищена для пользователя: " + user.getEmail());
        }
    }

    public List<OrderDTO> getOrdersByUser(String userEmail) {
        List<Order> orders = orderRepository.findByUserEmail(userEmail);
        return orders.stream().map(this::convertToDTO).toList();
    }

    public Page<OrderDTO> getOrdersByUser(String userEmail, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> ordersPage = orderRepository.findByUserEmail(userEmail, pageable);
        return ordersPage.map(this::convertToDTO);
    }

    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));
        return convertToDTO(order);
    }

    public Page<OrderDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> ordersPage = orderRepository.findAll(pageable);
        return ordersPage.map(this::convertToDTO);
    }

    public OrderDTO updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));
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

        // Добавляем информацию о пользователе в orderDTO
        UserDTO userDTO = new UserDTO();
        User user = order.getUser();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());

        // Если у пользователя есть адрес, добавляем его
        if (user.getAddress() != null) {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setCountry(user.getAddress().getCountry());
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setStreet(user.getAddress().getStreet());
            addressDTO.setHouse(user.getAddress().getHouse());
            addressDTO.setApartment(user.getAddress().getApartment());
            addressDTO.setPostalCode(user.getAddress().getPostalCode());

            userDTO.setAddress(addressDTO);
        }

        orderDTO.setOwner(userDTO);
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

