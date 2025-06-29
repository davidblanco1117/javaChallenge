package ar.com.francoblanco.challenge.infraestructure.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.francoblanco.challenge.domain.dto.OrderItemRequest;
import ar.com.francoblanco.challenge.domain.dto.OrderRequest;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getOrders_integration() throws Exception {
   
        mockMvc.perform(get("/get-orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
    
    @Test
    void getCatalog_integration() throws Exception {
    	  mockMvc.perform(get("/get-catalog"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
    
    @Test
    void process_integration() throws Exception {
        // Given - Crear un pedido válido
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setItemId("P-001");
        itemRequest.setQuantity(2);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderId("INTEGRATION-ORDER-001");
        orderRequest.setCustomerId("CUST-001");
        orderRequest.setOrderAmount(BigDecimal.valueOf(1000.00)); // 2 * 500.00 (precio del P-001)
        orderRequest.setOrderItems(Arrays.asList(itemRequest));

        // When & Then - Manejar respuesta asíncrona
        MvcResult mvcResult = mockMvc.perform(post("/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
            .andExpect(status().isOk())
            .andReturn();

        // Verificar que la respuesta es asíncrona
        assertTrue(mvcResult.getRequest().isAsyncStarted());
        
        // Obtener el resultado asíncrono
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().string("PROCESSED"));
    }
   
}
