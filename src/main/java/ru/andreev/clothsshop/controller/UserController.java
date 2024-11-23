package ru.andreev.clothsshop.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.dto.UserDTO;
import ru.andreev.clothsshop.model.User;
import ru.andreev.clothsshop.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public UserDTO getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        return userService.getUserProfile(email);
    }

    @PutMapping("/profile")
    public UserDTO updateUserProfile(@RequestBody UserDTO userDTO, Authentication authentication) {
        String email = authentication.getName();
        return userService.updateUserProfile(email, userDTO);
    }

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

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteUserProfile(Authentication authentication) {
        String email = authentication.getName();
        userService.deleteUserProfile(email);
        return ResponseEntity.noContent().build();
    }
}