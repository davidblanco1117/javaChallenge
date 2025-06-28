package ar.com.francoblanco.challenge.domain.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {

	
	private String idProduct;
	private String description;
	private BigDecimal price;

}
