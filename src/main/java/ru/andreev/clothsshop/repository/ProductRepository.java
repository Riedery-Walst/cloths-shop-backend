package ru.andreev.clothsshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andreev.clothsshop.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}