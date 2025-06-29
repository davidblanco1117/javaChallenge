package ar.com.francoblanco.challenge.infraestructure.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ar.com.francoblanco.challenge.domain.dto.OrderRequest;
import ar.com.francoblanco.challenge.infraestructure.service.OrderService;

@RestController
public class OrderController {

	@Autowired
	private OrderService orderService;
	
    public CompletableFuture<ResponseEntity<String>> process(@RequestBody OrderRequest request) {
        return orderService.process(request)
            .thenApply(orderResult -> ResponseEntity.ok(orderResult.getStatus()));
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
