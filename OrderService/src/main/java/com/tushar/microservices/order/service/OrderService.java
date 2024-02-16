package com.tushar.microservices.order.service;

import java.util.List;

import com.tushar.microservices.order.dto.OrderRequest;
import com.tushar.microservices.order.dto.OrderResponse;

public interface OrderService {

	String create(OrderRequest orderReq);

	List<OrderResponse> read();

	OrderResponse read(Long id);

	String update(OrderRequest orderReq);

	String delete(Long id);
}
