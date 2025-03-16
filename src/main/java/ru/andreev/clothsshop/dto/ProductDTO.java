package ru.andreev.clothsshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    private List<ColorDTO> colors = new ArrayList<>();
    private List<SizeDTO> sizes = new ArrayList<>();
    private List<String> photos = new ArrayList<>();
}