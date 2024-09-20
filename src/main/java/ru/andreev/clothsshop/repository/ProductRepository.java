package ru.andreev.clothsshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andreev.clothsshop.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Найти все товары по ID категории
    List<Product> findByCategoryId(Long categoryId);

    // Найти товары по их цене в заданном диапазоне
    List<Product> findByPriceBetween(double minPrice, double maxPrice);

    // Найти товары по цвету
    List<Product> findByColors_Id(Long colorId);

    // Найти товары по размеру
    List<Product> findBySizes_Id(Long sizeId);
}