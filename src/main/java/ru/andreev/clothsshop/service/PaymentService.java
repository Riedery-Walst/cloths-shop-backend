package ru.andreev.clothsshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.andreev.clothsshop.dto.PaymentRequest;
import ru.andreev.clothsshop.dto.PaymentResponse;
import ru.andreev.clothsshop.model.Order;
import ru.andreev.clothsshop.model.Payment;
import ru.andreev.clothsshop.repository.OrderRepository;
import ru.andreev.clothsshop.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Value("${yookassa.shop-id}")
    private String SHOP_ID;

    @Value("${yookassa.secret-key}")
    private String SECRET_KEY;

    @Value("${yookassa.return-url}")
    private String RETURN_URL;

    private static final String API_URL = "https://api.yookassa.ru/v3/payments";

    public PaymentService(PaymentRepository paymentRepository, OrderService orderService, ObjectMapper objectMapper, OrderRepository orderRepository, RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        // Найти заказ по ID
        Order order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        BigDecimal requestAmount = new BigDecimal(paymentRequest.getAmount().getValue());
        BigDecimal orderAmount = BigDecimal.valueOf(order.getTotalPrice());

        // Сравниваем суммы
        if (requestAmount.compareTo(orderAmount) != 0) {
            throw new IllegalArgumentException("Incorrect amount for order");
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", Map.of(
                "value", String.format("%.2f", order.getTotalPrice()),
                "currency", "RUB"
        ));
        requestBody.put("confirmation", Map.of(
                "type", "redirect",
                "return_url", paymentRequest.getReturnUrl()
        ));
        requestBody.put("description", "Оплата заказа №" + order.getId());
        requestBody.put("capture", true);  // Автоматическое подтверждение платежа
        requestBody.put("metadata", Map.of("order_id", order.getId()));

        String idempotenceKey = UUID.randomUUID().toString();

        // Заголовки для запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(SHOP_ID, SECRET_KEY);
        headers.set("Idempotence-Key", idempotenceKey); // Basic Auth для запроса в YooKassa

        // Создание запроса
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Отправка POST-запроса на создание платежа
            ResponseEntity<PaymentResponse> responseEntity = restTemplate.exchange(
                    API_URL, HttpMethod.POST, requestEntity, PaymentResponse.class);

            // Получение ответа от YooKassa через PaymentResponse DTO
            PaymentResponse paymentResponse = responseEntity.getBody();

            if (paymentResponse != null) {
                // Преобразуем PaymentResponse в сущность Payment и сохраняем
                Payment payment = mapToPaymentEntity(paymentResponse, paymentRequest, order);
                paymentRepository.save(payment);  // Сохраняем Payment

                // Привязываем Payment к Order и сохраняем Order
                order.setPayment(payment);
                orderRepository.save(order);  // Сохраняем Order
            }

            return paymentResponse;
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("An unexpected error occurred: " + e.getStatusCode() + " : " + e.getResponseBodyAsString());
        }
    }

    private Payment mapToPaymentEntity(PaymentResponse paymentResponse, PaymentRequest paymentRequest, Order order) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentResponse.getId());  // ID платежа
        payment.setConfirmationUrl(paymentResponse.getConfirmation().getConfirmation_url());  // URL для подтверждения
        payment.setStatus(paymentResponse.getStatus());  // Статус платежа
        payment.setAmount(Double.parseDouble(paymentRequest.getAmount().getValue()));  // Сумма
        payment.setCurrency(paymentRequest.getAmount().getCurrency());  // Валюта
        payment.setDescription("Оплата заказа №" + order.getId());  // Описание платежа
        payment.setCreatedAt(LocalDateTime.now());  // Дата создания платежа
        payment.setOrder(order);  // Связываем платеж с заказом
        return payment;
    }

}