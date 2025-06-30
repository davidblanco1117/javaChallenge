package ar.com.francoblanco.challenge.infraestructure.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ar.com.francoblanco.challenge.domain.model.OrderResult;

class OrderStorageTest {

    private OrderStorage orderStorage;
    private OrderResult order1;
    private OrderResult order2;
    private OrderResult order3;

    @BeforeEach
    void setUp() {
        orderStorage = new OrderStorage();
        
        order1 = new OrderResult("ORDER-001", "PROCESSED", 150, "Order processed successfully", LocalDateTime.now());
        order2 = new OrderResult("ORDER-002", "ERROR", 0, "Product not found", LocalDateTime.now());
        order3 = new OrderResult("ORDER-003", "PROCESSED", 200, "Order processed successfully", LocalDateTime.now());
    }

    @Test
    void testSave_NewOrder() {
        // When
        orderStorage.save(order1);

        // Then
        assertEquals(1, orderStorage.size());
        assertTrue(orderStorage.contains("ORDER-001"));
        assertEquals(order1, orderStorage.getById("ORDER-001"));
    }

    @Test
    void testSave_DuplicateOrder() {
        // Given
        orderStorage.save(order1);
        
        // When - Intentar guardar la misma orden nuevamente
        orderStorage.save(order1);

        // Then - No debe duplicarse
        assertEquals(1, orderStorage.size());
        assertTrue(orderStorage.contains("ORDER-001"));
    }

    @Test
    void testGetById_ExistingOrder() {
        // Given
        orderStorage.save(order1);

        // When
        OrderResult result = orderStorage.getById("ORDER-001");

        // Then
        assertNotNull(result);
        assertEquals("ORDER-001", result.getOrderId());
        assertEquals("PROCESSED", result.getStatus());
    }

    @Test
    void testGetById_NonExistingOrder() {
        // When
        OrderResult result = orderStorage.getById("NON-EXISTENT");

        // Then
        assertNull(result);
    }

    @Test
    void testGetInInsertionOrder_AllOrders() {
        // Given
        orderStorage.save(order1);
        orderStorage.save(order2);
        orderStorage.save(order3);

        // When
        List<OrderResult> result = orderStorage.getInInsertionOrder(null);

        // Then
        assertEquals(3, result.size());
        assertEquals("ORDER-001", result.get(0).getOrderId());
        assertEquals("ORDER-002", result.get(1).getOrderId());
        assertEquals("ORDER-003", result.get(2).getOrderId());
    }

    @Test
    void testGetInInsertionOrder_FilterByProcessed() {
        // Given
        orderStorage.save(order1);
        orderStorage.save(order2);
        orderStorage.save(order3);

        // When
        List<OrderResult> result = orderStorage.getInInsertionOrder("PROCESSED");

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(o -> "PROCESSED".equals(o.getStatus())));
        assertEquals("ORDER-001", result.get(0).getOrderId());
        assertEquals("ORDER-003", result.get(1).getOrderId());
    }

    @Test
    void testGetInInsertionOrder_FilterByError() {
        // Given
        orderStorage.save(order1);
        orderStorage.save(order2);
        orderStorage.save(order3);

        // When
        List<OrderResult> result = orderStorage.getInInsertionOrder("ERROR");

        // Then
        assertEquals(1, result.size());
        assertEquals("ORDER-002", result.get(0).getOrderId());
        assertEquals("ERROR", result.get(0).getStatus());
    }

    @Test
    void testGetInInsertionOrder_FilterByNonExistingStatus() {
        // Given
        orderStorage.save(order1);
        orderStorage.save(order2);

        // When
        List<OrderResult> result = orderStorage.getInInsertionOrder("NON-EXISTENT-STATUS");

        // Then
        assertEquals(0, result.size());
    }

    @Test
    void testGetInInsertionOrder_CaseInsensitive() {
        // Given
        orderStorage.save(order1);
        orderStorage.save(order2);

        // When
        List<OrderResult> result = orderStorage.getInInsertionOrder("processed");

        // Then
        assertEquals(1, result.size());
        assertEquals("ORDER-001", result.get(0).getOrderId());
    }

    @Test
    void testContains_ExistingOrder() {
        // Given
        orderStorage.save(order1);

        // When & Then
        assertTrue(orderStorage.contains("ORDER-001"));
    }

    @Test
    void testContains_NonExistingOrder() {
        // When & Then
        assertFalse(orderStorage.contains("NON-EXISTENT"));
    }

    @Test
    void testSize_EmptyStorage() {
        // When & Then
        assertEquals(0, orderStorage.size());
    }

    @Test
    void testSize_WithOrders() {
        // Given
        orderStorage.save(order1);
        orderStorage.save(order2);

        // When & Then
        assertEquals(2, orderStorage.size());
    }

    @Test
    void testClear() {
        // Given
        orderStorage.save(order1);
        orderStorage.save(order2);
        assertEquals(2, orderStorage.size());

        // When
        orderStorage.clear();

        // Then
        assertEquals(0, orderStorage.size());
        assertFalse(orderStorage.contains("ORDER-001"));
        assertFalse(orderStorage.contains("ORDER-002"));
        assertTrue(orderStorage.getInInsertionOrder(null).isEmpty());
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        // Given
        int numThreads = 10;
        int ordersPerThread = 100;
        Thread[] threads = new Thread[numThreads];

        // When - Crear múltiples threads que agreguen órdenes
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < ordersPerThread; j++) {
                    OrderResult order = new OrderResult(
                        "ORDER-" + threadId + "-" + j,
                        "PROCESSED",
                        100,
                        "Order processed",
                        LocalDateTime.now()
                    );
                    orderStorage.save(order);
                }
            });
            threads[i].start();
        }

        // Esperar a que todos los threads terminen
        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        assertEquals(numThreads * ordersPerThread, orderStorage.size());
        
        // Verificar que todas las órdenes están presentes
        for (int i = 0; i < numThreads; i++) {
            for (int j = 0; j < ordersPerThread; j++) {
                assertTrue(orderStorage.contains("ORDER-" + i + "-" + j));
            }
        }
    }

    @Test
    void testInsertionOrderPreserved() {
        // Given
        orderStorage.save(order1);
        orderStorage.save(order2);
        orderStorage.save(order3);

        // When
        List<OrderResult> result = orderStorage.getInInsertionOrder(null);

        // Then - El orden de inserción debe preservarse
        assertEquals(3, result.size());
        assertEquals("ORDER-001", result.get(0).getOrderId());
        assertEquals("ORDER-002", result.get(1).getOrderId());
        assertEquals("ORDER-003", result.get(2).getOrderId());
    }

    @Test
    void testFilteredInsertionOrderPreserved() {
        // Given
        orderStorage.save(order1);
        orderStorage.save(order2);
        orderStorage.save(order3);

        // When
        List<OrderResult> result = orderStorage.getInInsertionOrder("PROCESSED");

        // Then - El orden de inserción debe preservarse incluso con filtro
        assertEquals(2, result.size());
        assertEquals("ORDER-001", result.get(0).getOrderId());
        assertEquals("ORDER-003", result.get(1).getOrderId());
    }
} 