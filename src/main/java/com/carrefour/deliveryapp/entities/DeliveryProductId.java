package com.carrefour.deliveryapp.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class DeliveryProductId implements Serializable {
    private Long deliveryId;
    private Long productId;
}
