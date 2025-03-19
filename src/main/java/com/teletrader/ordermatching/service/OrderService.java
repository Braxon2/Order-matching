package com.teletrader.ordermatching.service;

import com.teletrader.ordermatching.dto.OrderBookResponse;
import com.teletrader.ordermatching.dto.OrderRequest;
import com.teletrader.ordermatching.dto.OrderResponse;
import com.teletrader.ordermatching.model.Order;
import com.teletrader.ordermatching.model.OrderType;
import com.teletrader.ordermatching.model.Trade;
import com.teletrader.ordermatching.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final Sinks.Many<OrderBookResponse> orderBookSink = Sinks.many().multicast().onBackpressureBuffer();

    public Mono<OrderResponse> processOrder(OrderRequest request) {

        if (!request.getType().equals("NEW_ORDER")) {
            return Mono.just(OrderResponse.builder()
                    .type("ORDER_RESPONSE")
                    .status("ERROR")
                    .message("Unknown message type").build());
        }


        List<String> emptyFields = new ArrayList<>();

        if (request.getType() == null) {
            emptyFields.add("Type");
        }

        if (request.getOrderType() == null) {
            emptyFields.add("Order type");
        }

        if (request.getPrice() == null) {
            emptyFields.add("Price");
        }

        if (request.getAmount() == null) {
            emptyFields.add("Amount");

        }

        if(!emptyFields.isEmpty()) {
            return Mono.just(OrderResponse.builder()
                    .type("ORDER_RESPONSE")
                    .status("ERROR")
                    .message("You need to fill this fields " + emptyFields).build());

        }

        if (request.getPrice() <= 0) {
            return Mono.just(OrderResponse.builder()
                    .type("ORDER_RESPONSE")
                    .status("ERROR")
                    .message("Price must be greater than zero")
                    .build());
        }

        if (request.getAmount() <= 0) {
            return Mono.just(OrderResponse.builder()
                    .type("ORDER_RESPONSE")
                    .status("ERROR")
                    .message("Amount must be greater than zero")
                    .build());
        }

        Order order = Order.create(request.getOrderType(), request.getPrice(), request.getAmount());

        return orderRepository.save(order)
                .flatMap(savedOrder -> {
                    if (savedOrder.getType() == OrderType.BUY) {
                        return matchBuyOrder(savedOrder);
                    } else {
                        return matchSellOrder(savedOrder);
                    }
                })
                .flatMap(this::updateOrderBook)
                .map(processedOrder -> OrderResponse.builder()
                        .type("ORDER_RESPONSE")
                        .id(processedOrder.getId())
                        .status("ACCEPTED")
                        .message("Order processed successfully")
                        .build());
    }

    private Mono<Order> matchBuyOrder(Order buyOrder) {
        return orderRepository.findMatchingSellOrders(buyOrder.getPrice())
                .filter(sellOrder -> buyOrder.getAmount() > 0)
                .flatMap(sellOrder -> executeMatch(buyOrder, sellOrder))
                .then(Mono.just(buyOrder));
    }

    private Mono<Order> matchSellOrder(Order sellOrder) {
        return orderRepository.findMatchingBuyOrders(sellOrder.getPrice())
                .filter(buyOrder -> sellOrder.getAmount() > 0)
                .flatMap(buyOrder -> executeMatch(buyOrder, sellOrder))
                .then(Mono.just(sellOrder));
    }

    private Mono<Trade> executeMatch(Order buyOrder, Order sellOrder) {
        double matchAmount = Math.min(buyOrder.getAmount(), sellOrder.getAmount());
        double matchPrice = sellOrder.getPrice();

        log.info("Matched orders: Buy={}, Sell={}, Amount={}, Price={}",
                buyOrder.getId(), sellOrder.getId(), matchAmount, matchPrice);


        buyOrder.setAmount(buyOrder.getAmount() - matchAmount);
        sellOrder.setAmount(sellOrder.getAmount() - matchAmount);

        Trade trade = Trade.create(buyOrder, sellOrder, matchPrice, matchAmount);

        Mono<Void> cleanup = Mono.empty();
        if (buyOrder.getAmount() <= 0) {
            cleanup = cleanup.then(orderRepository.delete(buyOrder));
        }
        if (sellOrder.getAmount() <= 0) {
            cleanup = cleanup.then(orderRepository.delete(sellOrder));
        }

        return cleanup.thenReturn(trade);
    }

    private Mono<Order> updateOrderBook(Order order) {
        return getOrderBook(10)
                .doOnNext(orderBookSink::tryEmitNext)
                .thenReturn(order);
    }

    public Mono<OrderBookResponse> getOrderBook(int limit) {
        return Mono.zip(
                orderRepository.findTopBuyOrders(limit).collectList(),
                orderRepository.findTopSellOrders(limit).collectList(),
                (buyOrders, sellOrders) -> OrderBookResponse.builder()
                        .type("ORDER_BOOK")
                        .buyOrders(mapToOrderDto(buyOrders))
                        .sellOrders(mapToOrderDto(sellOrders))
                        .build()
        );
    }

    private List<OrderBookResponse.OrderDto> mapToOrderDto(List<Order> orders) {
        return orders.stream()
                .map(OrderBookResponse.OrderDto::from)
                .collect(Collectors.toList());
    }

    public Flux<OrderBookResponse> getOrderBookUpdates() {
        return orderBookSink.asFlux();
    }



}
