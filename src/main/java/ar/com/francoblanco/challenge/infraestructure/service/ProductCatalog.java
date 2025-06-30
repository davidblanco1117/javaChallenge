package ar.com.francoblanco.challenge.infraestructure.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import ar.com.francoblanco.challenge.domain.model.Product;
import jakarta.annotation.PostConstruct;

@Component
public class ProductCatalog {

    private final Map<String, Product> inventory = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        inventory.put("P-001", new Product("P-001", "Teclado Mecánico",BigDecimal.valueOf(500.00)));
        inventory.put("P-002", new Product("P-002", "Mouse Inalámbrico", BigDecimal.valueOf(300.00)));
        inventory.put("P-003", new Product("P-003", "Monitor 24 pulgadas", BigDecimal.valueOf(1200.00)));
        }

    public Product getProduct(String productId) {
        return inventory.get(productId);
    }

    public boolean containsProduct(String productId) {
        return inventory.containsKey(productId);
    }

    public Collection<Product> getAllProducts() {
        return inventory.values();
    }
}