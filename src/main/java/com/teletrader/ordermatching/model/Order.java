package com.teletrader.ordermatching.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String id;
    private OrderType type;
    private double price;
    private double amount;
    private LocalDateTime timestamp;

    public static Order create(OrderType type, double price, double amount) {
        return Order.builder()
                .id(UUID.randomUUID().toString())
                .type(type)
                .price(price)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
