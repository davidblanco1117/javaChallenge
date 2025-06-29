package ar.com.francoblanco.challenge.domain.model;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {

	@Schema(description = "ID del producto", example = "P-001")
	private String idProduct;

	@Schema(description = "Descripcion del producto", example = "Teclado Mecánico")
	private String description;

	@Schema(description = "Precio del producto", example = "500")
	private BigDecimal price;

}
