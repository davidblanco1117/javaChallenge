package ar.com.francoblanco.challenge.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderResult {
    private String orderId;
    private String status; // PROCESSED, ERROR, TIMEOUT, VALIDATION_FAILED
    private long processingTimeMs;
    private String message;
    private LocalDateTime processedAt;
    
    public static OrderResult success(String orderId, long processingTime) {
        return new OrderResult(orderId, "PROCESSED", processingTime, "Order processed successfully", LocalDateTime.now());
    }
    
    public static OrderResult error(String orderId, String message) {
        return new OrderResult(orderId, "ERROR", 0, message, LocalDateTime.now());
    }

	public OrderResult(String orderId, String status) {
		this.orderId = orderId;
		this.status = status;
	}
}