package ru.andreev.clothsshop.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;


    @Enumerated(EnumType.STRING)
    private Role role;

    // Конструкторы, геттеры и сеттеры
}