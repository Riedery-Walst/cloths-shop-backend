package ru.andreev.clothsshop.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.andreev.clothsshop.dto.PaymentRequest;
import ru.andreev.clothsshop.dto.PaymentResponse;
import ru.andreev.clothsshop.model.Order;
import ru.andreev.clothsshop.model.OrderStatus;
import ru.andreev.clothsshop.model.Payment;
import ru.andreev.clothsshop.repository.OrderRepository;
import ru.andreev.clothsshop.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Value("${yookassa.shop-id}")
    private String SHOP_ID;

    @Value("${yookassa.secret-key}")
    private String SECRET_KEY;

    @Value("${yookassa.return-url}")
    private String RETURN_URL;

    private static final String API_URL = "https://api.yookassa.ru/v3/payments";

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        Order order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        BigDecimal requestAmount = new BigDecimal(paymentRequest.getAmount().getValue());
        BigDecimal orderAmount = BigDecimal.valueOf(order.getTotalPrice());

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
                "return_url", RETURN_URL
        ));
        requestBody.put("description", "Оплата заказа №" + order.getId());
        requestBody.put("capture", true);
        requestBody.put("metadata", Map.of("order_id", order.getId()));

        String idempotenceKey = UUID.randomUUID().toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(SHOP_ID, SECRET_KEY);
        headers.set("Idempotence-Key", idempotenceKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<PaymentResponse> responseEntity = restTemplate.exchange(
                    API_URL, HttpMethod.POST, requestEntity, PaymentResponse.class);

            PaymentResponse paymentResponse = responseEntity.getBody();

            if (paymentResponse != null) {
                Payment payment = mapToPaymentEntity(paymentResponse, paymentRequest, order);
                paymentRepository.save(payment);

                order.setPayment(payment);
                orderRepository.save(order);
            }

            return paymentResponse;
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("An unexpected error occurred: " + e.getStatusCode() + " : " + e.getResponseBodyAsString());
        }
    }

    public void checkPaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        String url = API_URL + "/" + payment.getPaymentId(); // API_URL + ID платежа

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(SHOP_ID, SECRET_KEY); // Авторизация с помощью BasicAuth
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Отправка запроса и проверка ответа
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, (Class<Map<String, Object>>)(Class<?>)Map.class
        );

        Map<String, Object> paymentData = response.getBody();

        // Проверка, что paymentData не null
        if (paymentData != null) {
            String status = (String) paymentData.get("status");

            // Обработка различных состояний платежа
            switch (status) {
                case "succeeded" -> {
                    // Если статус изменился на "succeeded" и платеж ещё не был обновлен
                    if (!"succeeded".equals(payment.getStatus())) {
                        payment.setStatus("succeeded");
                        Order order = payment.getOrder();
                        order.setStatus(OrderStatus.PAID); // Изменяем статус заказа на "PAID"
                        paymentRepository.save(payment);
                        orderRepository.save(order);
                        // Логируем успешное завершение платежа
                        log.info("Payment succeeded for order: " + order.getId());
                    }
                }
                case "canceled" -> {
                    // Если статус изменился на "canceled"
                    if (!"canceled".equals(payment.getStatus())) {
                        payment.setStatus("canceled");
                        Order order = payment.getOrder();
                        order.setStatus(OrderStatus.CANCELED); // Изменяем статус заказа на "CANCELED"
                        paymentRepository.save(payment);
                        orderRepository.save(order);
                        // Логируем отмену платежа
                        log.info("Payment canceled for order: " + payment.getOrder().getId());
                    }
                }
                case "waiting_for_capture" -> {
                    // Если статус в ожидании
                    if (!"waiting_for_capture".equals(payment.getStatus())) {
                        payment.setStatus("waiting_for_capture");
                        paymentRepository.save(payment);
                        // Логируем, что платеж в ожидании
                        log.info("Payment is waiting for capture for order: " + payment.getOrder().getId());
                    }
                }
                case null, default ->
                    // Логируем, если статус непредсказуемый или новый
                        log.warn("Received unexpected payment status: " + status + " for payment ID: " + payment.getId());
            }
        } else {
            // Логируем ошибку, если данные не получены
            throw new RuntimeException("Failed to retrieve payment data from YooKassa");
        }
    }

    @Scheduled(fixedRate = 60000) // Проверка каждые 60 секунд
    public void checkPendingPayments() {
        // Проверяем все платежи со статусом "pending"
        List<Payment> pendingPayments = paymentRepository.findByStatus("pending");
        for (Payment payment : pendingPayments) {
            checkPaymentStatus(payment.getId());
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