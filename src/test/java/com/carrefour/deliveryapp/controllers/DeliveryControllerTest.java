package com.carrefour.deliveryapp.controllers;

import com.carrefour.deliveryapp.dtos.BookingDTO;
import com.carrefour.deliveryapp.dtos.DeliveryDTO;
import com.carrefour.deliveryapp.dtos.DeliverySlotDTO;
import com.carrefour.deliveryapp.dtos.ProductDTO;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "salahedd.lahmam@gmail.com")
public class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService deliveryService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDTO bookingDTO;
    private DeliveryDTO deliveryDTO;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setCode("FR");
        productDTO.setQuantity(78);

        bookingDTO = new BookingDTO();
        bookingDTO.setCustomerId(1L);
        bookingDTO.setDeliverySlotId(1L);
        bookingDTO.setDeliveryAddress("Casa, oulfa");
        bookingDTO.setProductDTOS(List.of(productDTO));

        deliveryDTO = new DeliveryDTO();
        deliveryDTO.setId(1L);
        deliveryDTO.setDeliverySlotId(1L);
        deliveryDTO.setDeliveryAddress("test delivery addr");
    }

    @Test
    void bookDelivery_ShouldReturnDeliveryDTO() throws Exception {
        when(deliveryService.bookDelivery(any(BookingDTO.class))).thenReturn(deliveryDTO);

        mockMvc.perform(post("/api/deliveries/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists())
                .andDo(print());

        verify(deliveryService, times(1)).bookDelivery(any(BookingDTO.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/delivery-slots"), any(DeliveryDTO.class));
    }

    @Test
    void getAvailableSlots_ShouldReturnListOfSlots() throws Exception {
        int dayIndex = 1;
        String mode = "DELIVERY";
        List<DeliverySlotDTO> slots = List.of(new DeliverySlotDTO());

        when(deliveryService.getAvailableSlots(dayIndex, mode)).thenReturn(slots);

        mockMvc.perform(get("/api/deliveries/available/{dayIndex}/{mode}", dayIndex, mode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andDo(print());

        verify(deliveryService, times(1)).getAvailableSlots(dayIndex, mode);
    }

    @Test
    void getDeliveryStatus_ShouldReturnDeliveryDTO() throws Exception {
        Long deliveryId = 1L;
        when(deliveryService.getDeliveryCurrentStatus(deliveryId)).thenReturn(DeliveryStatusEnum.READY.toString());

        mockMvc.perform(get("/api/deliveries/state/{deliveryId}", deliveryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(jsonPath("$").exists())
                .andDo(print());

        verify(deliveryService, times(1)).getDeliveryCurrentStatus(deliveryId);
    }

    @Test
    void modifyDelivery_ShouldReturnUpdatedDeliveryDTO() throws Exception {
        Long deliveryId = 1L;
        Long newSlotId = 2L;
        String newAddress = "Casa, oulfa";
        when(deliveryService.updateDelivery(deliveryId, newSlotId, newAddress)).thenReturn(deliveryDTO);

        mockMvc.perform(patch("/api/deliveries/modify/{deliveryId}", deliveryId)
                        .param("newSlotId", newSlotId.toString())
                        .param("newAddress", newAddress)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists())
                .andDo(print());

        verify(deliveryService, times(1)).updateDelivery(deliveryId, newSlotId, newAddress);
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), any(DeliveryDTO.class));
    }
}