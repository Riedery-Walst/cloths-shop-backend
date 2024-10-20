package ru.andreev.clothsshop.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String paymentId;  // ID платежа, полученный от YooKassa
    private String confirmationUrl;  // URL для подтверждения платежа

    private double amount;

    private String currency;

    private String status;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "payment")
    private Order order;

}