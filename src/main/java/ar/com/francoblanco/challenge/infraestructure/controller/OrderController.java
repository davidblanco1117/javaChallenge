package ar.com.francoblanco.challenge.infraestructure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ar.com.francoblanco.challenge.domain.dto.OrderRequest;
import ar.com.francoblanco.challenge.infraestructure.service.OrderService;

@RestController
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	@PostMapping("/process")
	public ResponseEntity<String> process(@RequestBody OrderRequest request){
		orderService.process(request);
		return ResponseEntity.ok("OK");
	}
	
	@GetMapping("/get-catalog")
	public ResponseEntity<?> getCatalog(){
		
		return ResponseEntity.ok(orderService.getCatalog());
	}
	@GetMapping("/get-orders")
	public ResponseEntity<Object> getOrders(){
		return ResponseEntity.ok(orderService.getOrders());
	}
	
}
