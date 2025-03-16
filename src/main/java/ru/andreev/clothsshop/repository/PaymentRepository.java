package ru.andreev.clothsshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreev.clothsshop.model.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatus(String status);
}