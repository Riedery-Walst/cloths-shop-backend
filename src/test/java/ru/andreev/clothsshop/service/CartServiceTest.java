package ru.andreev.clothsshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.andreev.clothsshop.dto.CartDTO;
import ru.andreev.clothsshop.model.Cart;
import ru.andreev.clothsshop.model.Product;
import ru.andreev.clothsshop.model.User;
import ru.andreev.clothsshop.repository.CartRepository;
import ru.andreev.clothsshop.repository.ProductRepository;
import ru.andreev.clothsshop.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class CartServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setEmail("testuser@example.com");

        cart = new Cart();
        cart.setId(1L);

        product = new Product();
        product.setId(1L);
        product.setPrice(100.0);
    }

    @Test
    void testAddProductToCart_Success() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        CartDTO cartDTO = cartService.addProductToCart("testuser@example.com", 1L, 2, 1L, 1L);

        assertNotNull(cartDTO);
        assertEquals(1, cartDTO.getItems().size());
        assertEquals(200.0, cartDTO.getTotalPrice());
    }
}
