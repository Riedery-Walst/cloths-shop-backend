package ru.andreev.clothsshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.andreev.clothsshop.dto.CartDTO;
import ru.andreev.clothsshop.dto.CartItemDTO;
import ru.andreev.clothsshop.model.Cart;
import ru.andreev.clothsshop.model.CartItem;
import ru.andreev.clothsshop.model.Product;
import ru.andreev.clothsshop.model.User;
import ru.andreev.clothsshop.repository.CartRepository;
import ru.andreev.clothsshop.repository.ProductRepository;
import ru.andreev.clothsshop.repository.UserRepository;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartService(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public CartDTO getCartByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createCartForUser(user));
        return convertToDTO(cart);
    }

    public CartDTO addProductToCart(String email, Long productId, int quantity, Long colorId, Long sizeId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createCartForUser(user));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId) &&
                        item.getColorId().equals(colorId) &&
                        item.getSizeId().equals(sizeId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setColorId(colorId);
            newItem.setSizeId(sizeId);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        cart.recalculateTotalPrice();
        return convertToDTO(cartRepository.save(cart));
    }

    public CartDTO removeProductFromCart(String email, Long cartItemId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        cart.recalculateTotalPrice();
        return convertToDTO(cartRepository.save(cart));
    }

    public CartDTO clearCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().clear();
        cart.recalculateTotalPrice();
        return convertToDTO(cartRepository.save(cart));
    }

    public CartDTO updateCartItemQuantity(String email, Long cartItemId, int quantity) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(quantity);
        cart.recalculateTotalPrice();

        return convertToDTO(cartRepository.save(cart));
    }

    private Cart createCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    private CartDTO convertToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setTotalPrice(cart.getTotalPrice());
        cartDTO.setItems(cart.getItems().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        return cartDTO;
    }

    private CartItemDTO convertToDTO(CartItem item) {
        CartItemDTO itemDTO = new CartItemDTO();
        itemDTO.setId(item.getId());
        itemDTO.setProductId(item.getProduct().getId());
        itemDTO.setQuantity(item.getQuantity());
        itemDTO.setColorId(item.getColorId());
        itemDTO.setSizeId(item.getSizeId());
        itemDTO.setSubtotal(item.getSubtotal());
        return itemDTO;
    }
}