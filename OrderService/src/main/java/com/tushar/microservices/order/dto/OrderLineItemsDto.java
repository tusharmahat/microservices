package com.tushar.microservices.order.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemsDto {
	private Long id;

	@NotBlank
	private String skuCode;

	@NotBlank
	private BigDecimal price;

	@NotBlank
	private int quantity;
}
