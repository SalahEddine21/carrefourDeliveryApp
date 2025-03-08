package com.carrefour.deliveryapp.controllers;

import com.carrefour.deliveryapp.dtos.DeliveryDTO;
import com.carrefour.deliveryapp.enums.DeliveryStatusEnum;
import com.carrefour.deliveryapp.services.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "salahedd.lahmam@gmail.com")
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService deliveryService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    private DeliveryDTO deliveryDTO;

    @BeforeEach
    void setUp() {
        deliveryDTO = new DeliveryDTO();
    }

    @Test
    void updateDeliveryStatus_ShouldReturnUpdatedDeliveryDTO() throws Exception {
        Long deliveryId = 1L;
        DeliveryStatusEnum status = DeliveryStatusEnum.DELIVERED;
        when(deliveryService.updateDeliveryStatus(deliveryId, status)).thenReturn(deliveryDTO);

        mockMvc.perform(patch("/api/admin/update-delivery-status/{deliveryId}", deliveryId)
                        .param("status", status.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists())
                .andDo(print());

        verify(deliveryService, times(1)).updateDeliveryStatus(deliveryId, status);
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), any(DeliveryDTO.class));
    }
}
