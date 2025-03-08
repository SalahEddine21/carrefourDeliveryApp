package com.carrefour.deliveryapp.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookingDTO {
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    @NotNull(message = "Delivery Slot ID is required")
    private Long deliverySlotId;
    @NotNull(message = "Delivery address is required")
    private String deliveryAddress;
    @Valid
    private List<ProductDTO> productDTOS;
}
