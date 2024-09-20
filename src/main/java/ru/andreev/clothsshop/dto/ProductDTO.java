package ru.andreev.clothsshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private Long categoryId;
    private List<Long> colorIds;
    private List<Long> sizeIds;
    private List<String> photos;
}