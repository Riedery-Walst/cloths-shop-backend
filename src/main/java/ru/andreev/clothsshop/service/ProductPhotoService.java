package ru.andreev.clothsshop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.andreev.clothsshop.model.Product;
import ru.andreev.clothsshop.model.ProductPhoto;
import ru.andreev.clothsshop.repository.ProductPhotoRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductPhotoService {

    @Value("${image.upload-directory}")
    private String uploadDirectory;

    private final ProductPhotoRepository productPhotoRepository;

    public ProductPhotoService(ProductPhotoRepository productPhotoRepository) {
        this.productPhotoRepository = productPhotoRepository;
    }

    // Сохранение фотографий на файловую систему и в базу данных
    public List<ProductPhoto> savePhotos(List<MultipartFile> photos, Product product) {
        return photos.stream()
                .map(photo -> savePhoto(photo, product))
                .collect(Collectors.toList());
    }

    // Сохранение одной фотографии
    public ProductPhoto savePhoto(MultipartFile file, Product product) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDirectory, fileName);
        try {
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath);

            ProductPhoto photo = new ProductPhoto();
            photo.setPhotoUrl(filePath.toString());
            photo.setProduct(product);

            return productPhotoRepository.save(photo);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении изображения", e);
        }
    }

    // Удаление фотографий с файловой системы и из базы данных
    public void deletePhotos(List<ProductPhoto> photos) {
        for (ProductPhoto photo : photos) {
            deletePhoto(photo);
        }
    }

    // Удаление одной фотографии
    public void deletePhoto(ProductPhoto photo) {
        try {
            Files.deleteIfExists(Paths.get(photo.getPhotoUrl()));
            productPhotoRepository.delete(photo);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при удалении изображения", e);
        }
    }
}