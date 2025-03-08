package com.carrefour.deliveryapp.services;

import com.carrefour.deliveryapp.dtos.BookingDTO;
import com.carrefour.deliveryapp.dtos.DeliveryDTO;
import com.carrefour.deliveryapp.dtos.DeliverySlotDTO;
import com.carrefour.deliveryapp.dtos.ProductDTO;
import com.carrefour.deliveryapp.entities.*;
import com.carrefour.deliveryapp.enums.DeliveryMode;
import com.carrefour.deliveryapp.enums.DeliveryStatusEnum;
import com.carrefour.deliveryapp.exceptions.DSMaxReservationException;
import com.carrefour.deliveryapp.exceptions.DeliveryNotEligibleToUpdateException;
import com.carrefour.deliveryapp.exceptions.NotFoundException;
import com.carrefour.deliveryapp.mappers.DeliveryMapper;
import com.carrefour.deliveryapp.mappers.DeliverySlotMapper;
import com.carrefour.deliveryapp.repositories.CustomerRepository;
import com.carrefour.deliveryapp.repositories.DeliverySlotRepository;
import com.carrefour.deliveryapp.repositories.DeliveryRepository;
import com.carrefour.deliveryapp.utils.DeliveryUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final CustomerRepository customerRepository;
    private final DeliverySlotRepository deliverySlotRepository;
    private final ProductService productService;
    private final DeliverySlotMapper deliverySlotMapper;
    private final DeliveryMapper deliveryMapper;

    @Retryable(
            retryFor = PessimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 500)
    )
    @Transactional
    public DeliveryDTO bookDelivery(BookingDTO bookingDTO) {
        Customer customer = customerRepository.findById(bookingDTO.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        DeliverySlot deliverySlot = getDeliverySlot(bookingDTO.getDeliverySlotId());

        return createDelivery(customer, deliverySlot, bookingDTO.getDeliveryAddress(), bookingDTO.getProductDTOS());
    }


    @Transactional
    public DeliveryDTO createDelivery(Customer customer, DeliverySlot deliverySlot,
                                      String deliveryAddress, List<ProductDTO> productDTOS) {
        List<Product> products = productService.verifyAndUpdateStock(productDTOS);
        Delivery delivery = new Delivery();
        delivery.setCustomer(customer);
        delivery.setDeliverySlot(deliverySlot);
        delivery.setCreatedAt(LocalDateTime.now());
        delivery.setDeliveryAddress(deliveryAddress);
        delivery.setStatus(DeliveryStatusEnum.ACCEPTED);

        List<DeliveryProduct> deliveryProducts = getDeliveryProduct(productDTOS, products, delivery);
        delivery.setDeliveryProducts(deliveryProducts);

        return deliveryMapper.toDTO(deliveryRepository.save(delivery));
    }

    private List<DeliveryProduct> getDeliveryProduct(List<ProductDTO> productDTOS, List<Product> products, Delivery delivery) {
        List<DeliveryProduct> deliveryProducts = new ArrayList<>();
        for (ProductDTO productDTO : productDTOS) {
            Product product = products.stream()
                    .filter(p -> p.getId().equals(productDTO.getId()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Product not found: " + productDTO.getId()));

            DeliveryProduct deliveryProduct = new DeliveryProduct();
            deliveryProduct.setDelivery(delivery);
            deliveryProduct.setProduct(product);
            deliveryProduct.setQuantity(productDTO.getQuantity());

            deliveryProducts.add(deliveryProduct);
        }
        return deliveryProducts;
    }

    @Retryable(
            retryFor = PessimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 500)
    )
    @Transactional
    public DeliveryDTO updateDelivery(Long deliveryId, Long deliverySlotId, String newDelVAddress) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException("Delivery not found"));

        // is delivery status not ready
        if (!DeliveryUtils.ELIGIBLE_DELV_STATUS_FOR_UPDATE.contains(delivery.getStatus())) {
            throw new DeliveryNotEligibleToUpdateException("Cannot modify a delivery once it's not READY.");
        }

        if (deliverySlotId != null) {
            DeliverySlot deliverySlot = getDeliverySlot(deliverySlotId);
            delivery.setDeliverySlot(deliverySlot);
        }

        if (newDelVAddress != null && !newDelVAddress.isEmpty()) {
            delivery.setDeliveryAddress(newDelVAddress);
        }

        deliveryRepository.save(delivery);
        return deliveryMapper.toDTO(delivery);
    }

    private DeliverySlot getDeliverySlot(Long deliverySlotId) {
        DeliverySlot deliverySlot = deliverySlotRepository.findById(deliverySlotId)
                .orElseThrow(() -> new NotFoundException("Delivery slot not found"));

        //Verifying if delivery slot max reservations achieved
        long currentReservations = deliveryRepository.countByDeliverySlotId(deliverySlotId);
        if (currentReservations >= deliverySlot.getMaxReservations()) {
            throw new DSMaxReservationException("Delivery slot is fully booked");
        }
        return deliverySlot;
    }

    public String getDeliveryCurrentStatus(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException("Delivery not found"));
        return delivery.getStatus().toString();
    }

    @Transactional
    public DeliveryDTO updateDeliveryStatus(Long deliveryId, DeliveryStatusEnum newStatus) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));

        delivery.setStatus(newStatus);
        deliveryRepository.save(delivery);

        return deliveryMapper.toDTO(delivery);
    }

    public List<DeliverySlotDTO> getAvailableSlots(Integer dayIndex, String deliveryMode){
        final DayOfWeek day = DayOfWeek.of(dayIndex);
        final DeliveryMode mode = DeliveryMode.fromString(deliveryMode);
        final List<DeliverySlot> availableSlots = deliverySlotRepository.
                findByDayAndModeAndMaxReservationsLessThan(day, mode, DeliveryUtils.DELIVERY_MAX_RESERVATION);
        return deliverySlotMapper.toDto(availableSlots);
    }
}