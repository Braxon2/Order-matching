package com.teletrader.ordermatching.config;

import com.teletrader.ordermatching.model.Order;
import com.teletrader.ordermatching.model.OrderType;
import com.teletrader.ordermatching.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TestDataConfig {

    private final OrderRepository orderRepository;

    @Bean
    public CommandLineRunner loadTestData() {
        return args -> {
            log.info("Loading test data...");


            createOrder(OrderType.BUY, 100.50, 10.0);
            createOrder(OrderType.BUY, 101.50, 5.0);
            createOrder(OrderType.BUY, 100.00, 15.0);
            createOrder(OrderType.BUY, 99.80, 20.0);
            createOrder(OrderType.BUY, 399.80, 25.0);
//            createOrder(OrderType.BUY, 420.80, 5.0);
//            createOrder(OrderType.BUY, 190.80, 15.0);
//            createOrder(OrderType.BUY, 230.80, 15.0);
//            createOrder(OrderType.BUY, 99.50, 25.0);
//            createOrder(OrderType.BUY, 245.50, 15.0);
//            createOrder(OrderType.BUY, 399.50, 20.0);


            createOrder(OrderType.SELL, 101.00, 7.0);
            createOrder(OrderType.SELL, 101.50, 5.0);
//            createOrder(OrderType.SELL, 101.50, 18.0);
            createOrder(OrderType.SELL, 193.50, 18.0);
            createOrder(OrderType.SELL, 230.50, 18.0);
//            createOrder(OrderType.SELL, 540.50, 18.0);
//            createOrder(OrderType.SELL, 4970.50, 18.0);
//            createOrder(OrderType.SELL, 102.00, 22.0);
//            createOrder(OrderType.SELL, 102.50, 30.0);
//            createOrder(OrderType.SELL, 302.50, 10.0);
//            createOrder(OrderType.SELL, 178.50, 17.0);

            log.info("Test data loaded successfully!");
        };
    }

    private void createOrder(OrderType type, double price, double amount) {
        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .type(type)
                .price(price)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .build();

        orderRepository.save(order).subscribe(
                savedOrder -> log.info("Created test order: {} {} at {} for {}",
                        savedOrder.getType(), savedOrder.getId(), savedOrder.getPrice(), savedOrder.getAmount()),
                error -> log.error("Failed to create test order", error)
        );
    }

}
