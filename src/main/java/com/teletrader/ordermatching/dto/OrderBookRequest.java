package com.teletrader.ordermatching.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookRequest {
    private String type = "GET_ORDER_BOOK";
}
