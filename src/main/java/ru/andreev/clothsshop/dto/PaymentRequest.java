package ru.andreev.clothsshop.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long orderId;
    private Amount amount;
    private String returnUrl;
}