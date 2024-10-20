package ru.andreev.clothsshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreev.clothsshop.model.Payment;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);
}