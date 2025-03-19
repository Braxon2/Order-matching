package com.teletrader.ordermatching.repository;

import com.teletrader.ordermatching.model.Order;
import com.teletrader.ordermatching.model.OrderType;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class OrderRepository {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public Mono<Order> findById(String id) {
        return Mono.justOrEmpty(orders.get(id));
    }

    public Mono<Order> save(Order order) {
        orders.put(order.getId(), order);
        return Mono.just(order);
    }

    public Mono<Void> delete(Order order) {
        orders.remove(order.getId());
        return Mono.empty();
    }

    public Flux<Order> findByType(OrderType type) {
        return Flux.fromIterable(orders.values())
                .filter((Order order) -> order.getType() == type);
    }

    public Flux<Order> findTopBuyOrders(int limit){
        return Flux.fromIterable(orders.values())
                .filter((Order order) -> order.getType() == OrderType.BUY)
                .sort(Comparator.comparing((Order::getPrice)).reversed()
                        .thenComparing(Order::getTimestamp))
                        .take(limit);
    }

    public Flux<Order> findTopSellOrders(int limit){
        return Flux.fromIterable(orders.values())
                .filter((Order order) -> order.getType() == OrderType.SELL)
                .sort(Comparator.comparing((Order::getPrice))
                        .thenComparing(Order::getTimestamp))
                .take(limit);
    }

    public Flux<Order> findMatchingBuyOrders(double sellPrice){
        return Flux.fromIterable(orders.values())
                .filter((Order order) -> order.getType() == OrderType.BUY && order.getPrice() >= sellPrice)
                .sort(Comparator.comparing(Order::getPrice).reversed()
                .thenComparing(Order::getTimestamp));

    }

    public Flux<Order> findMatchingSellOrders(double buyPrice) {
        return Flux.fromIterable(orders.values())
                .filter(order -> order.getType() == OrderType.SELL && order.getPrice() <= buyPrice)
                .sort(Comparator.comparing(Order::getPrice)
                        .thenComparing(Order::getTimestamp));
    }




}
