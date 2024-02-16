package com.tushar.microservices.order.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.tushar.microservices.order.dto.InventoryResponse;
import com.tushar.microservices.order.dto.OrderLineItemsDto;
import com.tushar.microservices.order.dto.OrderRequest;
import com.tushar.microservices.order.dto.OrderResponse;
import com.tushar.microservices.order.entity.Order;
import com.tushar.microservices.order.entity.OrderLineItems;
import com.tushar.microservices.order.event.OrderPlacedEvent;
import com.tushar.microservices.order.repo.OrderRepo;
import com.tushar.microservices.order.service.OrderService;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepo orderRepo;

	private final Tracer tracer;

	private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

	private final WebClient.Builder webClientBuilder;

	@Override
	public String create(OrderRequest orderReq) {
		Order newOrder = new Order();
		newOrder.setOrderNumber(UUID.randomUUID().toString());

		List<OrderLineItems> orderLineItems = orderReq.getOrderLineItemsDtoList().stream().map(orderLineItemDto -> {
			OrderLineItems orderLineItem = new OrderLineItems();
			BeanUtils.copyProperties(orderLineItemDto, orderLineItem);
			orderLineItem.setOrder(newOrder);
			return orderLineItem;
		}).toList();

		newOrder.setOrderLineItems(orderLineItems);

		List<String> skuCodes = newOrder.getOrderLineItems().stream().map(OrderLineItems::getSkuCode).toList();

		Span inventorySpan = tracer.nextSpan().name("InventoryServiceLookup");
		log.info("Calling inventory service");
		try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventorySpan.start())) {
			// .uri("http://localhost:8090/api/inventory",
			InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
					.uri("http://inventory-service/api/inventory",
							uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
					.retrieve().bodyToMono(InventoryResponse[].class).block();

			boolean result = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

			if (result) {
				Order saveOrder = orderRepo.save(newOrder);
				kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(saveOrder.getOrderNumber()));
				if (saveOrder != null) {
					return saveOrder.getId().toString();
				}
			}

			throw new IllegalArgumentException("Product is not in stock");
		} finally {
			inventorySpan.end();
		}

	}

	@Override
	public List<OrderResponse> read() {
		List<Order> orderList = orderRepo.findAll();
		List<OrderResponse> orderResponseList = orderList.stream().map(o -> {
			OrderResponse orderRespose = new OrderResponse();
			BeanUtils.copyProperties(o, orderRespose);

			List<OrderLineItemsDto> oLineItemsDto = o.getOrderLineItems().stream().map(orderLineItem -> {
				OrderLineItemsDto oLineItemDto = new OrderLineItemsDto();
				BeanUtils.copyProperties(orderLineItem, oLineItemDto);
				return oLineItemDto;
			}).toList();

			orderRespose.setOrderLineItemsDtoList(oLineItemsDto);
			return orderRespose;
		}).toList();
		return orderResponseList;
	}

	@Override
	public OrderResponse read(Long id) {
		Order order = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
		OrderResponse orderRespose = new OrderResponse();
		BeanUtils.copyProperties(order, orderRespose);

		List<OrderLineItemsDto> oLineItemsDto = order.getOrderLineItems().stream().map(orderLineItem -> {
			OrderLineItemsDto oLineItemDto = new OrderLineItemsDto();
			BeanUtils.copyProperties(orderLineItem, oLineItemDto);
			return oLineItemDto;
		}).toList();

		orderRespose.setOrderLineItemsDtoList(oLineItemsDto);
		return orderRespose;
	}

	@Override
	public String update(OrderRequest orderReq) {
		Order order = orderRepo.findById(orderReq.getId()).orElseThrow(() -> new RuntimeException("Order not found"));

		List<OrderLineItems> orderLineItems = new ArrayList<>();

		orderReq.getOrderLineItemsDtoList().stream().forEach(orderLineItemDto -> {
			OrderLineItems orderLineItem = new OrderLineItems();
			BeanUtils.copyProperties(orderLineItemDto, orderLineItem);
			orderLineItem.setOrder(order);
			orderLineItems.add(orderLineItem);
		});

		order.setOrderLineItems(orderLineItems);
		Order saveOrder = orderRepo.save(order);
		if (saveOrder != null) {
			return "success";
		}
		return "failed";
	}

	@Override
	public String delete(Long id) {
		orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
		orderRepo.deleteById(id);
		return "deleted";
	}

}
