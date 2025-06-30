package ar.com.francoblanco.challenge.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderItemRequest {

	@Schema(description = "ID del producto", example = "P-001")
	private String itemId;
	@Schema(description = "Cantidad", example = "2")
	private Integer quantity;
}
