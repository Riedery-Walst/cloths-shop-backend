package ru.andreev.clothsshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.dto.PaymentRequest;
import ru.andreev.clothsshop.dto.PaymentResponse;
import ru.andreev.clothsshop.model.Payment;
import ru.andreev.clothsshop.service.PaymentService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);
        return ResponseEntity.ok(paymentResponse);
    }
    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long paymentId) {
        Optional<Payment> payment = paymentService.getPaymentById(paymentId);
        return payment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Получение всех платежей
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

}