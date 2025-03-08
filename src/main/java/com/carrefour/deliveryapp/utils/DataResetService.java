package com.carrefour.deliveryapp.utils;

import com.carrefour.deliveryapp.repositories.CustomerRepository;
import com.carrefour.deliveryapp.repositories.DeliverySlotRepository;
import com.carrefour.deliveryapp.repositories.DeliveryRepository;
import com.carrefour.deliveryapp.repositories.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DataResetService {

    @PersistenceContext
    private EntityManager entityManager;

    private final CustomerRepository customerRepository;
    private final DeliveryRepository deliveryRepository;
    private final DeliverySlotRepository deliverySlotRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void resetDBDATA() {
        deliveryRepository.deleteAll();
        customerRepository.deleteAll();
        deliverySlotRepository.deleteAll();
        productRepository.deleteAll();

        entityManager.createNativeQuery("ALTER TABLE customer AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE delivery_slot AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE delivery AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE product AUTO_INCREMENT = 1").executeUpdate();
    }
}
