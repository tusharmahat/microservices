package com.tushar.microservices.inventory.service;

import java.util.List;

import com.tushar.microservices.inventory.dto.InventoryResponse;

public interface InventoryService {

	List<InventoryResponse> isInStock(List<String> skuCode);

}
