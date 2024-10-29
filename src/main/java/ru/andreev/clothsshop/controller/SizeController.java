package ru.andreev.clothsshop.controller;

import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.model.Size;
import ru.andreev.clothsshop.service.SizeService;

import java.util.List;

@RestController
@RequestMapping("/api/sizes")
public class SizeController {
    private final SizeService sizeService;

    public SizeController(SizeService sizeService) {
        this.sizeService = sizeService;
    }

    // Получить все размеры
    @GetMapping
    public List<Size> getAllSizes() {
        return sizeService.getAllSizes();
    }

    // Получить размер по ID
    @GetMapping("/{id}")
    public Size getSizeById(@PathVariable Long id) {
        return sizeService.getSizeById(id);
    }
}