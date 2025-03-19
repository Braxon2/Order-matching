package com.teletrader.ordermatching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String type = "ORDER_RESPONSE";
    private String id;
    private String status;
    private String message;
}
