package ru.andreev.clothsshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andreev.clothsshop.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Найти категорию по имени
    Category findByName(String name);
}