package ru.andreev.clothsshop.controller;

import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.model.Cart;
import ru.andreev.clothsshop.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Получить корзину
    @GetMapping("/{cartId}")
    public Cart getCart(@PathVariable Long cartId) {
        return cartService.getCart(cartId);
    }

    // Добавить продукт в корзину
    @PostMapping("/{cartId}/add")
    public Cart addProductToCart(@PathVariable Long cartId, @RequestParam Long productId, @RequestParam int quantity) {
        return cartService.addProductToCart(cartId, productId, quantity);
    }

    // Удалить продукт из корзины
    @DeleteMapping("/{cartId}/remove/{cartItemId}")
    public Cart removeProductFromCart(@PathVariable Long cartId, @PathVariable Long cartItemId) {
        return cartService.removeProductFromCart(cartId, cartItemId);
    }

    // Очистить корзину
    @DeleteMapping("/{cartId}/clear")
    public Cart clearCart(@PathVariable Long cartId) {
        return cartService.clearCart(cartId);
    }
}