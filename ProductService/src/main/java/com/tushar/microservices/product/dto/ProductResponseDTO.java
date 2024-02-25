package com.tushar.microservices.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductResponseDTO {
	private String id;
	
	@NotBlank
	private String name;

	@NotBlank
	private String description;

	@NotNull
	private BigDecimal price;
}
