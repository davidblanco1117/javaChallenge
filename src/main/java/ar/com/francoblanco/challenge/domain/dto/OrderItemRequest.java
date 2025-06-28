package ar.com.francoblanco.challenge.domain.dto;

import lombok.Data;

@Data
public class OrderItemRequest {

	private String itemId;
	private Integer quantity;
}
