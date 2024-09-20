package ru.andreev.clothsshop.service;

import org.springframework.stereotype.Service;
import ru.andreev.clothsshop.model.Color;
import ru.andreev.clothsshop.repository.ColorRepository;

import java.util.List;

@Service
public class ColorService {

    private final ColorRepository colorRepository;

    public ColorService(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }

    public Color getColorById(Long id) {
        return colorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Color not found"));
    }

    public Color addColor(Color color) {
        return colorRepository.save(color);
    }

    public Color updateColor(Long id, Color updatedColor) {
        return colorRepository.findById(id)
                .map(color -> {
                    color.setName(updatedColor.getName());
                    return colorRepository.save(color);
                })
                .orElseThrow(() -> new IllegalArgumentException("Color not found"));
    }

    public void deleteColor(Long id) {
        if (!colorRepository.existsById(id)) {
            throw new IllegalArgumentException("Color not found");
        }
        colorRepository.deleteById(id);
    }
}