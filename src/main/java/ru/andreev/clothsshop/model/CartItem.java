package ru.andreev.clothsshop.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "cart-items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }
}