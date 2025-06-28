package ar.com.francoblanco.challenge.infraestructure.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ar.com.francoblanco.challenge.domain.dto.OrderItemRequest;
import ar.com.francoblanco.challenge.domain.dto.OrderRequest;
import ar.com.francoblanco.challenge.domain.model.Product;

@Service
public class OrderService {

	@Autowired
	private ProductCatalog catalog;

	@Autowired
	private OrderStorage orderStorage;

	@Async("executorTareas")
	public void process(OrderRequest request) {
		BigDecimal totalAmount = BigDecimal.valueOf(0);
		
		for (OrderItemRequest item : request.getOrderItems()) {

			if (!catalog.containsProduct(item.getItemId())) {
				System.out.println("No existe " + item.getItemId());
				return;
			}
			totalAmount = totalAmount
					.add(catalog.getProduct(item.getItemId()).getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
			
		
		
		}
		if(!totalAmount.equals(request.getOrderAmount())) {
			
			//Asumo que si el orderAmount es el precio total pagado, y si este difiere del
			//valor unitario del producto por la cantidad, entonces se trata de un error
			
			System.out.println("El total no coincide");

			System.out.println("totalAmount: " + totalAmount );
			System.out.println("requst totalAmount: " + request.getOrderAmount() );
			return;
		}
		
		orderStorage.save(request);
	}
	
	public Collection<Product> getCatalog() {
		return  catalog.getAllProducts();
	}
	
	public List<OrderRequest> getOrders(){
		return orderStorage.getAllInInsertionOrder();
	}
}
