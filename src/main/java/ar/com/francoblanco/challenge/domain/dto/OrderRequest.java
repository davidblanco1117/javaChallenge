package ar.com.francoblanco.challenge.domain.dto;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class OrderRequest {
	
	 @Schema(description = "ID de la orden", example = "123")
	 private String orderId;
	 @Schema(description = "ID del consumidor", example = "CUS-001")
	 private String customerId;
	 @Schema(description = "Valor total a pagar (Debe coincidir con el precio del producto por la cantidad)", example = "1000")
	 private BigDecimal orderAmount;
	 @Schema(description = "Lista de items a comprar")
	 private List<OrderItemRequest> orderItems;
	
}
