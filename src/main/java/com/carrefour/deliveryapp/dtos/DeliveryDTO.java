package com.carrefour.deliveryapp.dtos;

import com.carrefour.deliveryapp.enums.DeliveryStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DeliveryDTO {
    @NotNull
    private Long id;
    private Long customerId;
    private Long deliverySlotId;
    private LocalDateTime createdAt;
    private DeliveryStatusEnum status;
    private String deliveryAddress;
    private List<ProductDTO> products;
}
