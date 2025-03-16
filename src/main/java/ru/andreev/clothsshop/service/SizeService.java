package ru.andreev.clothsshop.service;

import org.springframework.stereotype.Service;
import ru.andreev.clothsshop.dto.SizeDTO;
import ru.andreev.clothsshop.model.Size;
import ru.andreev.clothsshop.repository.SizeRepository;

import java.util.List;

@Service
public class SizeService {

    private final SizeRepository sizeRepository;

    public SizeService(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }

    public Size getSizeById(Long id) {
        return sizeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Size not found"));
    }

    public SizeDTO addSize(SizeDTO sizeDTO) {
        Size size = new Size();
        size.setName(sizeDTO.getName());
        Size savedSize = sizeRepository.save(size);
        return new SizeDTO(savedSize.getId(), savedSize.getName());
    }

    public SizeDTO updateSize(Long id, SizeDTO updatedSizeDTO) {
        return sizeRepository.findById(id)
                .map(size -> {
                    size.setName(updatedSizeDTO.getName());
                    Size updatedSize = sizeRepository.save(size);
                    return new SizeDTO(updatedSize.getId(), updatedSize.getName());
                })
                .orElseThrow(() -> new IllegalArgumentException("Size not found"));
    }

    public void deleteSize(Long id) {
        if (!sizeRepository.existsById(id)) {
            throw new IllegalArgumentException("Size not found");
        }
        sizeRepository.deleteById(id);
    }
}