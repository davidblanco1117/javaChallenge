package ar.com.francoblanco.challenge.domain.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;


@Data
public class OrderRequest {

	 private String orderId;
	 private String customerId;
	 private BigDecimal orderAmount;
	 private List<OrderItemRequest> orderItems;
	
}
