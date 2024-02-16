package com.tushar.microservices.order.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
	private Long id;

	@NotNull
	private List<OrderLineItemsDto> orderLineItemsDtoList = new ArrayList<>();

}
