package com.tushar.microservices.product.service;

import java.util.Map;

import com.tushar.microservices.product.dto.ProductRequestDTO;
import com.tushar.microservices.product.dto.ProductResponseDTO;

public interface ProductService {

	String create(ProductRequestDTO product);

	Map<String, Object> read();

	ProductResponseDTO read(String id) throws RuntimeException;

	String update(ProductRequestDTO product) throws RuntimeException;

	String delete(String id) throws RuntimeException;

}
