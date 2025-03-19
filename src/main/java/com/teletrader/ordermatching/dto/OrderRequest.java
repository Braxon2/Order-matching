package com.teletrader.ordermatching.dto;

import com.teletrader.ordermatching.model.OrderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private String type;
    private OrderType orderType;
    private Double price;
    private Double amount;
}
