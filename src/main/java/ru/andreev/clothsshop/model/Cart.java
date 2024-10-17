package ru.andreev.clothsshop.model;

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