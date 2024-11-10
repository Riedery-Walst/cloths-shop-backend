package ru.andreev.clothsshop.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.andreev.clothsshop.model.Role;
import ru.andreev.clothsshop.model.User;
import ru.andreev.clothsshop.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@test.com";
            String adminPassword = "adminpassword";

            // Проверяем, существует ли уже администратор
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setPhone("000-000-0000");
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
                System.out.println("Администратор создан: " + adminEmail);
            } else {
                System.out.println("Администратор уже существует: " + adminEmail);
            }
        };
    }
}