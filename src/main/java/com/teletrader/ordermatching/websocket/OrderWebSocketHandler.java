package com.teletrader.ordermatching.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teletrader.ordermatching.dto.OrderBookResponse;
import com.teletrader.ordermatching.dto.OrderRequest;
import com.teletrader.ordermatching.dto.OrderResponse;
import com.teletrader.ordermatching.model.OrderType;
import com.teletrader.ordermatching.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderWebSocketHandler implements WebSocketHandler {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(this::processMessage)
                .flatMap(response -> {
                    try {
                        String json = objectMapper.writeValueAsString(response);
                        return session.send(Mono.just(session.textMessage(json)));
                    } catch (JsonProcessingException e) {
                        log.error("Failed to serialize response", e);
                        return Mono.empty();
                    }
                })
                .then();


        Flux<OrderBookResponse> orderBookUpdates = orderService.getOrderBookUpdates();
        Mono<Void> output = session.send(
                orderBookUpdates.flatMap(update -> {
                    try {
                        String json = objectMapper.writeValueAsString(update);
                        return Mono.just(session.textMessage(json));
                    } catch (JsonProcessingException e) {
                        log.error("Failed to serialize order book update", e);
                        return Mono.empty();
                    }
                })
        );


        return Mono.zip(input, output).then();
    }

    private Mono<?> processMessage(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            if (!jsonNode.has("type") || jsonNode.get("type").isNull()) {
                return Mono.just(OrderResponse.builder()
                        .type("ORDER_RESPONSE")
                        .status("ERROR")
                        .message("Message type is required")
                        .build());
            }

            String type = jsonNode.get("type").asText();

            switch (type) {
                case "NEW_ORDER":
                    OrderRequest orderRequest = objectMapper.treeToValue(jsonNode, OrderRequest.class);
                    return orderService.processOrder(orderRequest);
                case "GET_ORDER_BOOK":
                    return orderService.getOrderBook(10);
                default:
                    log.warn("Unknown message type: {}", type);
                    return Mono.just(OrderResponse.builder()
                            .type("ORDER_RESPONSE")
                            .status("ERROR")
                            .message("Unknown message type")
                            .build());
            }
        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
            return Mono.just(OrderResponse.builder()
                    .type("ORDER_RESPONSE")
                    .status("ERROR")
                    .message("Failed to process message")
                    .build());
        }
    }

}
