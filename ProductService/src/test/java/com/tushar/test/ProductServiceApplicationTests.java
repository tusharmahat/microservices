package com.tushar.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tushar.microservices.product.ProductServiceApplication;
import com.tushar.microservices.product.entity.Product;
import com.tushar.microservices.product.repo.ProductRepo;

@SpringBootTest(classes = ProductServiceApplication.class)
@Testcontainers // for instance of test containers
@AutoConfigureMockMvc // autoconfig mockmvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // order test methods
class ProductServiceApplicationTests {
	private static String savedProductId;
	@Container
	static MongoDBContainer mongoDbContainter = new MongoDBContainer("mongo:6.0.13");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	ProductRepo productRepo;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDbContainter::getReplicaSetUrl);
	}

	@Test
	@Order(1)
	void shouldCreateProduct() throws Exception {
		// change object to json
		String productJsonString = objectMapper.writeValueAsString(createProduct());

		// trigger docker container to save the product in the mongodb instance
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON).content(productJsonString)).andExpect(status().isCreated())
				.andReturn();
		// get the response as string
		String responseBody = mvcResult.getResponse().getContentAsString();
		// extract the item from the json
		savedProductId = objectMapper.readTree(responseBody).get("message").asText();
		// check if the the product is saved or not
		assertEquals(1, productRepo.findAll().size());
	}

	@Test
	@Order(2)
	void shouldGetProduct() throws Exception {
		// check get endpoint
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product/" + savedProductId)).andExpect(status().isOk());
		assertEquals(savedProductId, productRepo.findById(savedProductId).get().getId());
	}

	@Test
	@Order(3)
	void shouldGetAllProducts() throws Exception {
		// check get endpoint
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")).andExpect(status().isOk());

		// test if get all returns all the data
		assertEquals(1, productRepo.findAll().size());
	}

	@Test
	@Order(4)
	void shouldUpdateProduct() throws Exception {
		// change object to json
		Product product = createProduct();

		// UPDATE
		product.setId(savedProductId);
		product.setName("UpdatedName");
		String productJsonString = objectMapper.writeValueAsString(product);

		// trigger docker container to save the product in the mongodb instance
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/product")
				.contentType(MediaType.APPLICATION_JSON).content(productJsonString)).andExpect(status().isOk())
				.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		String result = objectMapper.readTree(responseBody).get("message").asText();
		// check if the the product is saved or not
		assertEquals("updated", result);
	}

	@Test
	@Order(5)
	void shouldDeleteProduct() throws Exception {
		// check get endpoint
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/product/" + savedProductId)).andExpect(status().isOk());

		// test if get all returns all the data
		assertEquals(0, productRepo.findAll().size());
	}

	Product createProduct() {
		Product product = new Product();
		product.setName("puma");
		product.setDescription("shoes");
		product.setPrice(new BigDecimal("100.99"));
		return product;
	}
}
