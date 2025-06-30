package ar.com.francoblanco.challenge.infraestructure.controller;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.com.francoblanco.challenge.domain.dto.OrderRequest;
import ar.com.francoblanco.challenge.domain.model.OrderResult;
import ar.com.francoblanco.challenge.domain.model.Product;
import ar.com.francoblanco.challenge.infraestructure.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping("/process")
	@Operation(summary = "Create a new order given a request body")
	public CompletableFuture<ResponseEntity<String>> process(@RequestBody OrderRequest request) {
		return orderService.process(request).thenApply(orderResult -> ResponseEntity.ok(orderResult.getStatus()));
	}

	@GetMapping("/get-catalog")
	@Operation(summary = "Get the catalog of available products")
	public ResponseEntity<Collection<Product>> getCatalog() {
		return ResponseEntity.ok(orderService.getCatalog());
	}

	@GetMapping("/get-orders")
	@Operation(summary = "Get the list of current orders", description = "Return all the orders processed. Allows filter by status")
	public ResponseEntity<List<OrderResult>> getOrders(
		@Parameter(description = "Filter by status")
		@RequestParam(value = "status", required = false) String status) {
		
		return ResponseEntity.ok(orderService.getOrders(status));
	}

}
