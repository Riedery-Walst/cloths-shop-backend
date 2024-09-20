package ru.andreev.clothsshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andreev.clothsshop.model.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
    Size findByName(String name);
}