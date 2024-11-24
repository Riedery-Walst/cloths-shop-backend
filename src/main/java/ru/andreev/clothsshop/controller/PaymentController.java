package ru.andreev.clothsshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.andreev.clothsshop.dto.PaymentRequest;
import ru.andreev.clothsshop.dto.PaymentResponse;
import ru.andreev.clothsshop.service.PaymentService;

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

    @GetMapping("/status/{paymentId}")
    public ResponseEntity<String> checkPaymentStatus(@PathVariable Long paymentId) {
        paymentService.checkPaymentStatus(paymentId);  // Обновляем статус платежа и заказа
        return ResponseEntity.ok("Payment status updated.");
    }
}