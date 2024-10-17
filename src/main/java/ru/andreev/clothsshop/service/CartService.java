package ru.andreev.clothsshop.service;

import org.springframework.stereotype.Service;
import ru.andreev.clothsshop.model.Cart;
import ru.andreev.clothsshop.model.CartItem;
import ru.andreev.clothsshop.model.Product;
import ru.andreev.clothsshop.repository.CartRepository;
import ru.andreev.clothsshop.repository.ProductRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId).orElseThrow(() -> new IllegalArgumentException("Cart not found"));
    }

    public Cart addProductToCart(Long cartId, Long productId, int quantity) {
        Cart cart = getCart(cartId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        cart.addItem(cartItem);

        return cartRepository.save(cart);
    }

    public Cart removeProductFromCart(Long cartId, Long cartItemId) {
        Cart cart = getCart(cartId);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        cart.removeItem(cartItem);

        return cartRepository.save(cart);
    }

    public Cart clearCart(Long cartId) {
        Cart cart = getCart(cartId);
        cart.getItems().clear();
        cart.recalculateTotalPrice();

        return cartRepository.save(cart);
    }
}