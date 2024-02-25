package com.tushar.microservices.product.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tushar.microservices.product.dto.ProductRequestDTO;
import com.tushar.microservices.product.dto.ProductResponseDTO;
import com.tushar.microservices.product.service.ProductService;

import jakarta.validation.Valid;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api/product")
public class RestController {

	@Autowired
	private ProductService productService;

	@PostMapping("")
	public ResponseEntity<Map<String, String>> createProduct(@Valid @RequestBody ProductRequestDTO product) {
		Map<String, String> response = new HashMap<>();
		String productCreate = productService.create(product);
		response.put("message", productCreate);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("")
	public ResponseEntity<Map<String, Object>> getProducts() {
		Map<String, Object> products = productService.read();
		return new ResponseEntity<>(products, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Map<String, ProductResponseDTO>> getProduct(@PathVariable("id") String id) throws Exception {
		System.out.println(id);
		ProductResponseDTO product = productService.read(id);
		Map<String, ProductResponseDTO> response = new HashMap<>();
		response.put("message", product);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("")
	public ResponseEntity<Map<String, String>> updateProduct(@Valid @RequestBody ProductRequestDTO product) throws Exception {
		String updateProduct = productService.update(product);
		Map<String, String> response = new HashMap<>();
		response.put("message", updateProduct);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable("id") String id) throws Exception {
		String deleteProduct = productService.delete(id);
		Map<String, String> response = new HashMap<>();
		response.put("message", deleteProduct);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
