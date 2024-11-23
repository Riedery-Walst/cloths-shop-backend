package ru.andreev.clothsshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private List<OrderItemDTO> items;
    private double totalPrice;
    private LocalDateTime createdDate;
    private String status;
}