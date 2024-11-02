package ru.andreev.clothsshop.dto;

import lombok.Data;

@Data
public class ProductFilterDTO {
    private Long colorId;
    private Long sizeId;
    private Long categoryId;
    private Double minPrice;
    private Double maxPrice;
}