package ru.andreev.clothsshop.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private List<CartItemDTO> items;
    private double totalPrice;
}