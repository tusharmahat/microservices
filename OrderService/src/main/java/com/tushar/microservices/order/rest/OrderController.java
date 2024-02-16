package com.tushar.microservices.order.rest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tushar.microservices.order.dto.OrderRequest;
import com.tushar.microservices.order.dto.OrderResponse;
import com.tushar.microservices.order.service.OrderService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
//	@CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
//	@TimeLimiter(name = "inventory")
//	@Retry(name = "inventory")
//	public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest) {
	public String placeOrder(@RequestBody OrderRequest orderRequest) {
//		return CompletableFuture.supplyAsync(() -> orderService.create(orderRequest));
		return  orderService.create(orderRequest);
	}

	public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
		return CompletableFuture.supplyAsync(() -> "Something went wrong, please order after sometime.");
	}

	@GetMapping("")
	@ResponseStatus(HttpStatus.OK)
	public List<OrderResponse> readOrder() {
		List<OrderResponse> orderList = orderService.read();
		return orderList;
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public OrderResponse readOrder(@PathVariable("id") Long id) {
		OrderResponse order = orderService.read(id);
		return order;
	}

	@PutMapping("")
	@ResponseStatus(HttpStatus.OK)
	public String readOrder(@RequestBody OrderRequest orderRequest) {
		return orderService.update(orderRequest);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public String deleteOrder(@PathVariable("id") Long id) {
		return orderService.delete(id);
	}

}
