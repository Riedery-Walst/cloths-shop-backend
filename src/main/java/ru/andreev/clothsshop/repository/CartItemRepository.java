package ru.andreev.clothsshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreev.clothsshop.model.CartItem;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Найти все элементы корзины по ID пользователя (если корзина связана с пользователем)
    List<CartItem> findByCartUserId(Long userId);

}