package ru.andreev.clothsshop.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private int quantity;
    private Long colorId;
    private Long sizeId;
    private double subtotal;
}