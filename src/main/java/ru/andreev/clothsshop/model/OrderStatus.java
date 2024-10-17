package ru.andreev.clothsshop.model;

public enum OrderStatus {
    PENDING,      // Ожидает подтверждения
    CONFIRMED,    // Подтвержден
    SHIPPED,      // Отправлен
    DELIVERED,    // Доставлен
    CANCELLED     // Отменен
}