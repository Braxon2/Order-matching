package com.teletrader.ordermatching.dto;

import com.teletrader.ordermatching.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookResponse {

    private String type = "ORDER_BOOK";
    private List<OrderDto> buyOrders;
    private List<OrderDto> sellOrders;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDto {
        private String id;
        private double price;
        private double amount;

        public static OrderDto from(Order order) {
            return new OrderDto(order.getId(), order.getPrice(), order.getAmount());
        }
    }

}
