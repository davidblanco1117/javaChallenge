package ar.com.francoblanco.challenge.infraestructure.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ar.com.francoblanco.challenge.domain.dto.OrderItemRequest;
import ar.com.francoblanco.challenge.domain.dto.OrderRequest;
import ar.com.francoblanco.challenge.domain.model.OrderResult;
import ar.com.francoblanco.challenge.domain.model.Product;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class OrderService {
    
    @Autowired
    private ProductCatalog catalog;
    
    @Autowired
    private OrderStorage orderStorage;
    
    @Autowired
    @Qualifier("executorTareas")
    private Executor taskExecutor;
    
    public CompletableFuture<OrderResult> process(OrderRequest request) {
        long startTime = System.currentTimeMillis();
        
        return CompletableFuture.supplyAsync(() -> {

            try {
                Thread.sleep(100 + new Random().nextInt(400));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Processing interrupted", e);
            }
            
            validateOrder(request);
            orderStorage.save(request);
            
            long processingTime = System.currentTimeMillis() - startTime;
            log.info("Order {} processed in {}ms", request.getOrderId(), processingTime);
            
            return OrderResult.success(request.getOrderId(), processingTime);
            
        }, taskExecutor)
        .exceptionally(throwable -> {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("Error processing order {}: {}", request.getOrderId(), throwable.getMessage());
            return OrderResult.error(request.getOrderId(), throwable.getMessage());
        });
    }
    
    private void validateOrder(OrderRequest request) {

        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (OrderItemRequest item : request.getOrderItems()) {
            if (!catalog.containsProduct(item.getItemId())) {
                throw new IllegalArgumentException("Product not found: " + item.getItemId());
            }
            totalAmount = totalAmount.add(
                catalog.getProduct(item.getItemId()).getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }
        
        if (!totalAmount.equals(request.getOrderAmount())) {
            throw new IllegalArgumentException("Order amount mismatch");
        }
    }
    
	public Collection<Product> getCatalog() {
		return catalog.getAllProducts();
	}

	public List<OrderRequest> getOrders() {
		return orderStorage.getAllInInsertionOrder();
	}
}