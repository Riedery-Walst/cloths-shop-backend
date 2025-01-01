package ru.andreev.clothsshop.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
    private Set<OrderItem> items = new HashSet<>();

    private double totalPrice;

    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    // Метод для добавления элементов заказа
    public void addItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
        recalculateTotalPrice();
    }

    // Метод для пересчета общей стоимости
    public void recalculateTotalPrice() {
        this.totalPrice = items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

}