package com.teletrader.ordermatching.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trade {
    private String id;
    private String buyOrderId;
    private String sellOrderId;
    private double price;
    private double amount;
    private LocalDateTime timestamp;

    public static Trade create(Order buyOrder, Order sellOrder, double price, double amount) {
        return Trade.builder()
                .id(UUID.randomUUID().toString())
                .buyOrderId(buyOrder.getId())
                .sellOrderId(sellOrder.getId())
                .price(price)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
