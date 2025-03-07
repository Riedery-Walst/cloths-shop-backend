package ru.andreev.clothsshop.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
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
import java.util.*;

@Service
@Slf4j
public class PaymentService {

    private static final String API_URL = "https://api.yookassa.ru/v3/payments";

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Value("${yookassa.shop-id}")
    private String SHOP_ID;

    @Value("${yookassa.secret-key}")
    private String SECRET_KEY;

    @Value("${yookassa.return-url}")
    private String RETURN_URL;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public Optional<Payment> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    // Метод для получения всех платежей
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Метод для проверки всех платежей с статусом "pending"
    @Transactional
    public void checkPendingPayments() {
        List<Payment> payments = paymentRepository.findByStatus("pending");
        payments.forEach(this::checkPaymentStatus); // Для каждого платежа со статусом "pending" вызываем проверку статуса
    }

    // Метод для проверки статуса конкретного платежа через YooKassa API
    public void checkPaymentStatus(Payment payment) {
        if (payment == null || payment.getPaymentId() == null) {
            log.warn("Invalid payment data.");
            return;
        }

        String url = API_URL + "/" + payment.getPaymentId();
        HttpEntity<String> entity = new HttpEntity<>(buildHttpHeaders(null));

        // Используем ParameterizedTypeReference для правильной десериализации ответа
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
                    }
            );

            // Обрабатываем ответ API и обновляем статусы
            handlePaymentStatus(response.getBody(), payment);
        } catch (HttpClientErrorException e) {
            log.error("Failed to retrieve payment data: " + e.getResponseBodyAsString(), e);
        }
    }

    // Обработка статуса платежа из ответа API
    private void handlePaymentStatus(Map<String, Object> paymentData, Payment payment) {
        if (paymentData != null) {
            String status = (String) paymentData.get("status");
            switch (status) {
                case "succeeded":
                    updatePaymentStatus(payment, OrderStatus.PAID, "succeeded");
                    break;
                case "canceled":
                    updatePaymentStatus(payment, OrderStatus.CANCELED, "canceled");
                    break;
                case "pending":
                    updatePaymentStatus(payment, OrderStatus.PENDING, "pending");
                    break;
                default:
                    log.warn("Unexpected payment status: " + status + " for payment ID: " + payment.getId());
            }
        } else {
            throw new RuntimeException("Failed to retrieve payment data from YooKassa");
        }
    }

    // Обновление статуса платежа и заказа
    private void updatePaymentStatus(Payment payment, OrderStatus orderStatus, String newStatus) {
        if (!newStatus.equals(payment.getStatus())) {
            payment.setStatus(newStatus);
            if (orderStatus != null) {
                Order order = payment.getOrder();
                order.setStatus(orderStatus);
                orderRepository.save(order);
            }
            paymentRepository.save(payment);
            log.info("Payment status updated to '" + newStatus + "' for order: " + payment.getOrder().getId());
        }
    }

    private HttpHeaders buildHttpHeaders(String idempotenceKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(SHOP_ID, SECRET_KEY);
        if (idempotenceKey != null) {
            headers.set("Idempotence-Key", idempotenceKey);
        }
        return headers;
    }

    // Метод для создания нового платежа через YooKassa
    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        Order order = getOrderOrThrow(paymentRequest.getOrderId());
        validatePaymentAmount(paymentRequest, order);

        Map<String, Object> requestBody = buildPaymentRequestBody(order);
        String idempotenceKey = UUID.randomUUID().toString();
        HttpHeaders headers = buildHttpHeaders(idempotenceKey);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        int maxRetries = 10;
        long initialDelay = 3000;
        double backoffMultiplier = 2.0;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                ResponseEntity<PaymentResponse> responseEntity = restTemplate.exchange(
                        API_URL, HttpMethod.POST, requestEntity, PaymentResponse.class
                );
                return handlePaymentResponse(responseEntity.getBody(), paymentRequest, order);
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    // Если ошибка 429, вычисляем задержку и ждем
                    long delay = (long) (initialDelay * Math.pow(backoffMultiplier, attempt));
                    try {
                        Thread.sleep(delay); // Приостанавливаем выполнение на вычисленное время
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread interrupted during backoff", ie);
                    }
                } else {
                    // Если это другая ошибка, выбрасываем исключение
                    throw new RuntimeException("An unexpected error occurred: " + e.getStatusCode() + " : " + e.getResponseBodyAsString());
                }
            }
        }

        // Если все попытки исчерпаны, выбрасываем исключение
        throw new RuntimeException("Max retries (" + maxRetries + ") exceeded for payment creation.");
    }

    private Order getOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    private void validatePaymentAmount(PaymentRequest paymentRequest, Order order) {
        BigDecimal requestAmount = new BigDecimal(paymentRequest.getAmount().getValue());
        BigDecimal orderAmount = BigDecimal.valueOf(order.getTotalPrice());

        if (requestAmount.compareTo(orderAmount) != 0) {
            throw new IllegalArgumentException("Incorrect amount for order");
        }
    }

    private Map<String, Object> buildPaymentRequestBody(Order order) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", Map.of("value", String.format("%.2f", order.getTotalPrice()), "currency", "RUB"));
        requestBody.put("confirmation", Map.of("type", "redirect", "return_url", RETURN_URL));
        requestBody.put("description", "Оплата заказа №" + order.getId());
        requestBody.put("capture", true);
        requestBody.put("metadata", Map.of("order_id", order.getId()));
        return requestBody;
    }

    private PaymentResponse handlePaymentResponse(PaymentResponse paymentResponse, PaymentRequest paymentRequest, Order order) {
        if (paymentResponse != null) {
            Payment payment = mapToPaymentEntity(paymentResponse, paymentRequest, order);
            paymentRepository.save(payment);
            order.setPayment(payment);
            orderRepository.save(order);
        }
        return paymentResponse;
    }

    private Payment mapToPaymentEntity(PaymentResponse paymentResponse, PaymentRequest paymentRequest, Order order) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentResponse.getId());
        payment.setConfirmationUrl(paymentResponse.getConfirmation().getConfirmation_url());
        payment.setStatus(paymentResponse.getStatus());
        payment.setAmount(Double.parseDouble(paymentRequest.getAmount().getValue()));
        payment.setCurrency(paymentRequest.getAmount().getCurrency());
        payment.setDescription("Оплата заказа №" + order.getId());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setOrder(order);
        return payment;
    }
}
