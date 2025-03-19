package com.teletrader.ordermatching.controllers;

import com.teletrader.ordermatching.dto.OrderBookResponse;
import com.teletrader.ordermatching.dto.OrderRequest;
import com.teletrader.ordermatching.dto.OrderResponse;
import com.teletrader.ordermatching.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;



@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/orders")
    public Mono<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        log.info("Received order request: {}", request);
        return orderService.processOrder(request);
    }

    @GetMapping("/orderbook")
    public Mono<OrderBookResponse> getOrderBook() {
        log.info("Received order book request");
        return orderService.getOrderBook(10);
    }
}
