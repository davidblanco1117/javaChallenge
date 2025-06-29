package ar.com.francoblanco.challenge.infraestructure.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.com.francoblanco.challenge.domain.dto.OrderItemRequest;
import ar.com.francoblanco.challenge.domain.dto.OrderRequest;
import ar.com.francoblanco.challenge.domain.model.OrderResult;
import ar.com.francoblanco.challenge.domain.model.Product;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductCatalog catalog;

    @Mock
    private OrderStorage orderStorage;

    @Mock
    private Executor taskExecutor;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest validOrderRequest;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product("P-001", "Test Product", BigDecimal.valueOf(100.00));
        
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setItemId("P-001");
        itemRequest.setQuantity(2);

        validOrderRequest = new OrderRequest();
        validOrderRequest.setOrderId("ORDER-001");
        validOrderRequest.setCustomerId("CUST-001");
        validOrderRequest.setOrderAmount(BigDecimal.valueOf(200.00));
        validOrderRequest.setOrderItems(Arrays.asList(itemRequest));
    }

    @Test
    void testProcessOrder_Success() throws Exception {
        // Given
        when(catalog.containsProduct("P-001")).thenReturn(true);
        when(catalog.getProduct("P-001")).thenReturn(testProduct);
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        // When
        CompletableFuture<OrderResult> future = orderService.process(validOrderRequest);
        OrderResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertNotNull(result);
        assertEquals("ORDER-001", result.getOrderId());
        assertEquals("PROCESSED", result.getStatus());
        assertTrue(result.getProcessingTimeMs() > 0);
        assertEquals("Order processed successfully", result.getMessage());
        
        verify(orderStorage).save(validOrderRequest);
    }

    @Test
    void testProcessOrder_ProductNotFound() throws Exception {
        // Given
        when(catalog.containsProduct("P-001")).thenReturn(false);
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        // When
        CompletableFuture<OrderResult> future = orderService.process(validOrderRequest);
        OrderResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertNotNull(result);
        assertEquals("ORDER-001", result.getOrderId());
        assertEquals("ERROR", result.getStatus());
        assertTrue(result.getMessage().contains("Product not found"));
        
        verify(orderStorage, never()).save(any());
    }

    @Test
    void testProcessOrder_AmountMismatch() throws Exception {
        // Given
        validOrderRequest.setOrderAmount(BigDecimal.valueOf(150.00)); // Wrong amount
        when(catalog.containsProduct("P-001")).thenReturn(true);
        when(catalog.getProduct("P-001")).thenReturn(testProduct);
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        // When
        CompletableFuture<OrderResult> future = orderService.process(validOrderRequest);
        OrderResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertNotNull(result);
        assertEquals("ORDER-001", result.getOrderId());
        assertEquals("ERROR", result.getStatus());
        assertTrue(result.getMessage().contains("Order amount mismatch"));
        
        verify(orderStorage, never()).save(any());
    }

    @Test
    void testProcessOrder_InterruptedException() throws Exception {
        // Given
        doAnswer(invocation -> {
            Thread.currentThread().interrupt();
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        // When
        CompletableFuture<OrderResult> future = orderService.process(validOrderRequest);
        OrderResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertNotNull(result);
        assertEquals("ERROR", result.getStatus());
        assertTrue(result.getMessage().contains("Processing interrupted"));
    }

    @Test
    void testGetCatalog() {
        // Given
        when(catalog.getAllProducts()).thenReturn(Arrays.asList(testProduct));

        // When
        var result = orderService.getCatalog();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(catalog).getAllProducts();
    }

    @Test
    void testGetOrders() {
        // Given
        when(orderStorage.getAllInInsertionOrder()).thenReturn(Arrays.asList(validOrderRequest));

        // When
        var result = orderService.getOrders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderStorage).getAllInInsertionOrder();
    }

    @Test
    void testProcessOrder_MultipleItems() throws Exception {
        // Given
        OrderItemRequest item1 = new OrderItemRequest();
        item1.setItemId("P-001");
        item1.setQuantity(1);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setItemId("P-002");
        item2.setQuantity(3);

        validOrderRequest.setOrderItems(Arrays.asList(item1, item2));
        validOrderRequest.setOrderAmount(BigDecimal.valueOf(400.00)); // 100 + (300 * 1)

        Product product2 = new Product("P-002", "Test Product 2", BigDecimal.valueOf(100.00));
        
        when(catalog.containsProduct("P-001")).thenReturn(true);
        when(catalog.containsProduct("P-002")).thenReturn(true);
        when(catalog.getProduct("P-001")).thenReturn(testProduct);
        when(catalog.getProduct("P-002")).thenReturn(product2);
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        // When
        CompletableFuture<OrderResult> future = orderService.process(validOrderRequest);
        OrderResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertNotNull(result);
        assertEquals("ORDER-001", result.getOrderId());
        assertEquals("PROCESSED", result.getStatus());
        
        verify(orderStorage).save(validOrderRequest);
    }
} 