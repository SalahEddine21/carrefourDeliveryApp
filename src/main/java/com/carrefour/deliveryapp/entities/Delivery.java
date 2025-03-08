package com.carrefour.deliveryapp.entities;

import com.carrefour.deliveryapp.enums.DeliveryStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "delivery_slot_id", nullable = false)
    private DeliverySlot deliverySlot;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryProduct> deliveryProducts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private DeliveryStatusEnum status;

    private String deliveryAddress;

    private LocalDateTime createdAt;
}
