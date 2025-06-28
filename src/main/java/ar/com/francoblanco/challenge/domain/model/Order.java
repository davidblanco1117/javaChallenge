package ar.com.francoblanco.challenge.domain.model;

import lombok.Data;

@Data
public class Order {
	
	private String orderId;
	private String customerId;
	private String itemId;
	private Integer quantity;
	
}
