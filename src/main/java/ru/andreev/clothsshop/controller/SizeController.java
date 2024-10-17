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

    // Добавить новый размер (доступно только для администраторов)
    @PostMapping("/admin/add")
    public Size addSize(@RequestBody Size size) {
        return sizeService.addSize(size);
    }

    // Обновить размер (доступно только для администраторов)
    @PutMapping("/admin/update/{id}")
    public Size updateSize(@PathVariable Long id, @RequestBody Size updatedSize) {
        return sizeService.updateSize(id, updatedSize);
    }

    // Удалить размер (доступно только для администраторов)
    @DeleteMapping("/admin/delete/{id}")
    public void deleteSize(@PathVariable Long id) {
        sizeService.deleteSize(id);
    }
}