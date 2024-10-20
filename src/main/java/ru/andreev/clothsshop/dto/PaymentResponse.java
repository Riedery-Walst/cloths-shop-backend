package ru.andreev.clothsshop.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String id;  // Идентификатор платежа
    private String status;
    private Confirmation confirmation;

    @Data
    public static class Confirmation {
        private String type;
        private String confirmation_url;
    }
}