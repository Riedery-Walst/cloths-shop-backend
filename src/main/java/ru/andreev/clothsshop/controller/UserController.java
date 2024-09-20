package ru.andreev.clothsshop.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.dto.UserDTO;
import ru.andreev.clothsshop.model.User;
import ru.andreev.clothsshop.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public UserDTO getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        return userService.getUserProfile(username);
    }

    // Обновить профиль пользователя
    @PutMapping("/profile")
    public UserDTO updateUserProfile(@RequestBody UserDTO userDTO, Authentication authentication) {
        String username = authentication.getName();
        return userService.updateUserProfile(username, userDTO);
    }

    // Регистрация нового пользователя
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserDTO userDTO) {
        User registeredUser = userService.registerUser(userDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PutMapping("/admin/promote")
    public ResponseEntity<User> promoteUserToAdmin(String email) {
        User promotedUser = userService.makeAdmin(email);
        return ResponseEntity.ok(promotedUser);
    }
}