package ru.andreev.clothsshop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cart", orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    private User user;

    private double totalPrice;

    // Добавить товар в корзину
    public void addItem(CartItem item) {
        this.items.add(item);
        item.setCart(this);
        recalculateTotalPrice();
    }

    // Удалить товар из корзины
    public void removeItem(CartItem item) {
        this.items.remove(item);
        recalculateTotalPrice();
    }

    // Пересчитать общую стоимость корзины
    public void recalculateTotalPrice() {
        this.totalPrice = items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }
}