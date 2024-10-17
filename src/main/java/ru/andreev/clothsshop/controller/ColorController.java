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

    // Добавить новый цвет (доступно только для администраторов)
    @PostMapping("/admin/add")
    public Color addColor(@RequestBody Color color) {
        return colorService.addColor(color);
    }

    // Обновить цвет (доступно только для администраторов)
    @PutMapping("/admin/update/{id}")
    public Color updateColor(@PathVariable Long id, @RequestBody Color updatedColor) {
        return colorService.updateColor(id, updatedColor);
    }

    // Удалить цвет (доступно только для администраторов)
    @DeleteMapping("/admin/delete/{id}")
    public void deleteColor(@PathVariable Long id) {
        colorService.deleteColor(id);
    }
}