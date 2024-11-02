package ru.andreev.clothsshop.service;

import org.springframework.stereotype.Service;
import ru.andreev.clothsshop.dto.ColorDTO;
import ru.andreev.clothsshop.model.Color;
import ru.andreev.clothsshop.repository.ColorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColorService {
    private final ColorRepository colorRepository;

    public ColorService(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    public List<ColorDTO> getAllColors() {
        return colorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ColorDTO getColorById(Long id) {
        return colorRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Цвет не найден с ID: " + id));
    }

    public ColorDTO createColor(ColorDTO colorDTO) {
        Color color = new Color();
        color.setName(colorDTO.getName());
        color.setHex(colorDTO.getHex());
        Color savedColor = colorRepository.save(color);
        return convertToDTO(savedColor);
    }

    public ColorDTO updateColor(Long id, ColorDTO colorDTO) {
        return colorRepository.findById(id).map(color -> {
            color.setName(colorDTO.getName());
            color.setHex(colorDTO.getHex());
            Color updatedColor = colorRepository.save(color);
            return convertToDTO(updatedColor);
        }).orElseThrow(() -> new RuntimeException("Цвет не найден с ID: " + id));
    }

    public void deleteColor(Long id) {
        colorRepository.deleteById(id);
    }

    private ColorDTO convertToDTO(Color color) {
        return new ColorDTO(color.getId(), color.getName(), color.getHex());
    }
}