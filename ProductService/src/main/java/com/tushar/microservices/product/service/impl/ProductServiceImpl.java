package com.tushar.microservices.product.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tushar.microservices.product.dto.ProductRequestDTO;
import com.tushar.microservices.product.dto.ProductResponseDTO;
import com.tushar.microservices.product.entity.Product;
import com.tushar.microservices.product.repo.ProductRepo;
import com.tushar.microservices.product.service.ProductService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductRepo productRepo;

	@Override
	public String create(ProductRequestDTO productCreateDTO) {
		Product product = new Product();
		BeanUtils.copyProperties(productCreateDTO, product);
		Product saveProduct = productRepo.insert(product);
		System.out.println(saveProduct);
		if (saveProduct != null) {
			 log.info("Product {} is saved", product.getId());
			return saveProduct.getId();
		}
		return "failed";
	}

	@Override
	public Map<String, Object> read() {
		Map<String, Object> response = new HashMap<>();
		List<Product> products = productRepo.findAll();
		if (products.size() != 0) {
			List<ProductResponseDTO> productsDetails = new ArrayList<>();
			products.forEach(product -> {
				ProductResponseDTO productDetail = new ProductResponseDTO();
				BeanUtils.copyProperties(product, productDetail);
				productsDetails.add(productDetail);
			});

			response.put("products", productsDetails);
		} else {
			response.put("products", "no products found");
		}
		return response;
	}

	@Override
	public ProductResponseDTO read(String id) throws RuntimeException {
		Product product = productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
		ProductResponseDTO productDetail = new ProductResponseDTO();
		BeanUtils.copyProperties(product, productDetail);
		return productDetail;
	}

	@Override
	public String update(ProductRequestDTO product) throws RuntimeException {
		Product existingProduct = productRepo.findById(product.getId()).orElseThrow(() -> new RuntimeException("Product not found"));
		BeanUtils.copyProperties(product, existingProduct);
		Product updateProduct = productRepo.save(existingProduct);
		if (updateProduct != null) {
			return "updated";
		}
		return "failed";
	}

	@Override
	public String delete(String id) throws RuntimeException {
		productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
		productRepo.deleteById(id);
		return "deleted";
	}

}
