package ru.andreev.clothsshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreev.clothsshop.model.ProductPhoto;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Long> {
}