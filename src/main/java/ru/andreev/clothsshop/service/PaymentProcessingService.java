package ru.andreev.clothsshop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentProcessingService {

    private final PaymentService paymentService;

    @Autowired
    public PaymentProcessingService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Scheduled(fixedRate = 60000)
    public void processPayments() {
        paymentService.checkPendingPayments();
    }

}
