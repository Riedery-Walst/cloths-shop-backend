package ru.andreev.clothsshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long productId;
    private int quantity;
    private double price;
    private Long colorId;
    private Long sizeId;
}