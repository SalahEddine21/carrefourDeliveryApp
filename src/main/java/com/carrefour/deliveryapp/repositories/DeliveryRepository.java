package com.carrefour.deliveryapp.repositories;

import com.carrefour.deliveryapp.entities.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    long countByDeliverySlotId(Long deliverySlotId);
}

