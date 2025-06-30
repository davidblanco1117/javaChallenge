package ar.com.francoblanco.challenge.infraestructure.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import ar.com.francoblanco.challenge.domain.model.OrderResult;

@Component
public class OrderStorage {
	

    private final ConcurrentMap<String, OrderResult> orders = new ConcurrentHashMap<>();
    private final Queue<String> insertionOrder = new ConcurrentLinkedQueue<>();

    public void save(OrderResult order) {
        if (orders.putIfAbsent(order.getOrderId(), order) == null) {
            insertionOrder.add(order.getOrderId());
        }
    }

    public OrderResult getById(String orderId) {
        return orders.get(orderId);
    }

    public List<OrderResult> getInInsertionOrder(String status) {
        List<OrderResult> orderedList = new ArrayList<>();
        for (String id : insertionOrder) {
        	OrderResult order = orders.get(id);
            if (order != null) {
            	if(status==null || (status!=null && status.equalsIgnoreCase(order.getStatus())))
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