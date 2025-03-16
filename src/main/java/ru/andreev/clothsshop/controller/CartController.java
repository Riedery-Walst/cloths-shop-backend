package ru.andreev.clothsshop.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.dto.CartDTO;
import ru.andreev.clothsshop.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartDTO getCart(Authentication authentication) {
        String email = authentication.getName();
        return cartService.getCartByUser(email);
    }

    @PostMapping("/add")
    public CartDTO addProductToCart(
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam(required = false) Long colorId,
            @RequestParam(required = false) Long sizeId,
            Authentication authentication) {
        String email = authentication.getName();
        return cartService.addProductToCart(email, productId, quantity, colorId, sizeId);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public CartDTO removeProductFromCart(
            @PathVariable Long cartItemId,
            Authentication authentication) {
        String email = authentication.getName();
        return cartService.removeProductFromCart(email, cartItemId);
    }

    @PatchMapping("/updateQuantity/{cartItemId}")
    public CartDTO updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestParam int quantity,
            Authentication authentication) {
        String email = authentication.getName();
        return cartService.updateCartItemQuantity(email, cartItemId, quantity);
    }

    @DeleteMapping("/clear")
    public CartDTO clearCart(Authentication authentication) {
        String email = authentication.getName();
        return cartService.clearCart(email);
    }
}