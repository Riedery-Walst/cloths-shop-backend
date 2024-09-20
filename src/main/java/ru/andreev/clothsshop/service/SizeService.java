package ru.andreev.clothsshop.service;

import org.springframework.stereotype.Service;
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

    public Size addSize(Size size) {
        return sizeRepository.save(size);
    }

    public Size updateSize(Long id, Size updatedSize) {
        return sizeRepository.findById(id)
                .map(size -> {
                    size.setName(updatedSize.getName());
                    return sizeRepository.save(size);
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