package com.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.MethodOrderer;
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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tushar.microservices.order.OrderServiceApplication;
import com.tushar.microservices.order.entity.Order;
import com.tushar.microservices.order.entity.OrderLineItems;
import com.tushar.microservices.order.repo.OrderRepo;

@SpringBootTest(classes=OrderServiceApplication.class)
@Testcontainers // for instance of test containers
@AutoConfigureMockMvc // autoconfig mockmvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // order test methods
class OrderServiceApplicationTests {

	@Container
	static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:8.0");

	@Autowired
	private OrderRepo orderRepository;

	static {
		MY_SQL_CONTAINER.start();
	}

	private static String savedOrderId;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	
	@DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url",() -> MY_SQL_CONTAINER.getJdbcUrl());
        registry.add("spring.datasource.username",() -> MY_SQL_CONTAINER.getUsername());
        registry.add("spring.datasource.password",() -> MY_SQL_CONTAINER.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto",() -> "create");

    }
	
	@Test
	@org.junit.jupiter.api.Order(1)
	void shouldCreateOrder() throws Exception, JsonProcessingException {

		// change object to json
		String orderJsonString = objectMapper.writeValueAsString(createOrder());

		// trigger docker container to save the product in the mongodb instance
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
				.contentType(MediaType.APPLICATION_JSON).content(orderJsonString)).andExpect(status().isCreated())
				.andReturn();
		// get the response as string
		String responseBody = mvcResult.getResponse().getContentAsString();
		// extract the item from the json
		savedOrderId = objectMapper.readTree(responseBody).asText();
		// check if the the product is saved or not
		assertEquals(1, orderRepository.findAll().size());

	}

	Order createOrder() {
		Order order = new Order();
		OrderLineItems lineItem1 = new OrderLineItems();
		lineItem1.setSkuCode("SKU001");
		lineItem1.setPrice(BigDecimal.TEN);
		lineItem1.setQuantity(2);
		lineItem1.setOrder(order);

		OrderLineItems lineItem2 = new OrderLineItems();
		lineItem2.setSkuCode("SKU002");
		lineItem2.setPrice(BigDecimal.valueOf(15));
		lineItem2.setQuantity(1);
		lineItem2.setOrder(order);

		order.getOrderLineItems().add(lineItem1);
		order.getOrderLineItems().add(lineItem2);

		return order;
	}

}
