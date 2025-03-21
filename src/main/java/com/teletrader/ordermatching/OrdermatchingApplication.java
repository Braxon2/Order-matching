package com.teletrader.ordermatching;

import com.teletrader.ordermatching.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class OrdermatchingApplication {

	@Autowired
	public OrderService orderService;

	public static void main(String[] args) {

		SpringApplication.run(OrdermatchingApplication.class, args);
	}




}
