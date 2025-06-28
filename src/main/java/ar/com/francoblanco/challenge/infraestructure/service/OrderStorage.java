package ar.com.francoblanco.challenge.infraestructure.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import ar.com.francoblanco.challenge.domain.dto.OrderRequest;
@Component
public class OrderStorage {
	

    private final ConcurrentMap<String, OrderRequest> orders = new ConcurrentHashMap<>();
    private final Queue<String> insertionOrder = new ConcurrentLinkedQueue<>();

    public void save(OrderRequest order) {
        if (orders.putIfAbsent(order.getOrderId(), order) == null) {
            insertionOrder.add(order.getOrderId());
        }
    }

    public OrderRequest getById(String orderId) {
        return orders.get(orderId);
    }

    public List<OrderRequest> getAllInInsertionOrder() {
        List<OrderRequest> orderedList = new ArrayList<>();
        for (String id : insertionOrder) {
        	OrderRequest order = orders.get(id);
            if (order != null) {
                orderedList.add(order);
            }
        }
        return orderedList;
    }

    public boolean contains(String orderId) {
        return orders.containsKey(orderId);
    }

    public int size() {
        return orders.size();
    }

    public void clear() {
        orders.clear();
        insertionOrder.clear();
    }
}