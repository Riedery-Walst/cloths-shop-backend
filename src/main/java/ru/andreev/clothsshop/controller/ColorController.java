package ru.andreev.clothsshop.controller;

import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.model.Color;
import ru.andreev.clothsshop.service.ColorService;

import java.util.List;

@RestController
@RequestMapping("/api/colors")
public class ColorController {
    private final ColorService colorService;

    public ColorController(ColorService colorService) {
        this.colorService = colorService;
    }

    // Получить все цвета
    @GetMapping
    public List<Color> getAllColors() {
        return colorService.getAllColors();
    }

    // Получить цвет по ID
    @GetMapping("/{id}")
    public Color getColorById(@PathVariable Long id) {
        return colorService.getColorById(id);
    }
}