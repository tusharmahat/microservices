package com.tushar.microservices.product.entity;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Document(value="Product")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Product {

	@Id
	@MongoId
	@Field(name = "id")
	private String id;

	@Field(name = "name")
	private String name;

	@Field(name = "description")
	private String description;

	@Field(name = "price")
	private BigDecimal price;
}
