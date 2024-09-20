package ru.andreev.clothsshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andreev.clothsshop.model.Color;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {

    // Найти цвет по имени
    Color findByName(String name);
}