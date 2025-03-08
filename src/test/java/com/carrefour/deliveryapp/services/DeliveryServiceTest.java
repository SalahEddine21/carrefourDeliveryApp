package com.carrefour.deliveryapp.services;

import com.carrefour.deliveryapp.dtos.BookingDTO;
import com.carrefour.deliveryapp.dtos.DeliveryDTO;
import com.carrefour.deliveryapp.dtos.DeliverySlotDTO;
import com.carrefour.deliveryapp.dtos.ProductDTO;
import com.carrefour.deliveryapp.entities.Customer;
import com.carrefour.deliveryapp.entities.Delivery;
import com.carrefour.deliveryapp.entities.DeliverySlot;
import com.carrefour.deliveryapp.entities.Product;
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
import jakarta.persistence.PessimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DeliverySlotRepository deliverySlotRepository;

    @Mock
    private DeliverySlotMapper deliverySlotMapper;

    @Mock
    private DeliveryMapper deliveryMapper;

    @Mock
    private ProductService productService;

    @InjectMocks
    private DeliveryService deliveryService;

    private Customer customer;
    private DeliverySlot deliverySlot;
    private BookingDTO bookingDTO;
    private DeliveryDTO deliveryDTO;
    private Product product;
    private Delivery delivery;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);

        deliverySlot = new DeliverySlot();
        deliverySlot.setId(1L);
        deliverySlot.setMaxReservations(5);

        deliveryDTO = new DeliveryDTO();
        deliveryDTO.setId(1L);
        deliveryDTO.setDeliveryAddress("Test Address");

        product = new Product();
        product.setId(1L);
        product.setCode("PRD1");

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setCode("FR");
        productDTO.setQuantity(78);

        delivery = new Delivery();
        delivery.setId(1L);
        delivery.setCustomer(customer);
        delivery.setDeliverySlot(deliverySlot);
        delivery.setCreatedAt(LocalDateTime.now());
        delivery.setDeliveryAddress("Test Address");
        delivery.setStatus(DeliveryStatusEnum.ACCEPTED);

        bookingDTO = new BookingDTO();
        bookingDTO.setCustomerId(1L);
        bookingDTO.setDeliverySlotId(1L);
        bookingDTO.setDeliveryAddress("Test Address");
        bookingDTO.setProductDTOS(List.of(productDTO));
    }

    // Test case: Successfully book a delivery
    @Test
    void shouldBookDeliverySuccessfully() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(deliverySlotRepository.findById(1L)).thenReturn(Optional.of(deliverySlot));
        when(deliveryRepository.countByDeliverySlotId(1L)).thenReturn(3L);
        when(productService.verifyAndUpdateStock(Mockito.anyList())).thenReturn(List.of(product));
        when(deliveryRepository.save(Mockito.any(Delivery.class))).thenReturn(delivery);
        when(deliveryMapper.toDTO(Mockito.any(Delivery.class))).thenReturn(deliveryDTO);

        DeliveryDTO result = deliveryService.bookDelivery(bookingDTO);

        assertNotNull(result);
        assertEquals(deliveryDTO.getId(), result.getId());
        assertEquals("Test Address", result.getDeliveryAddress());
    }

    // Test case: Book delivery - Customer not found
    @Test
    void shouldThrowNotFoundExceptionWhenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> deliveryService.bookDelivery(bookingDTO));
    }

    // Test case: Book delivery - Delivery slot not found
    @Test
    void shouldThrowNotFoundExceptionWhenDeliverySlotNotFound_bookDelivery() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(deliverySlotRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> deliveryService.bookDelivery(bookingDTO));
    }

    // Test case: Book delivery - Delivery slot fully booked
    @Test
    void shouldThrowDSMaxReservationExceptionWhenSlotIsFull() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(deliverySlotRepository.findById(1L)).thenReturn(Optional.of(deliverySlot));
        when(deliveryRepository.countByDeliverySlotId(1L)).thenReturn(5L); // Max reservations reached

        assertThrows(DSMaxReservationException.class, () -> deliveryService.bookDelivery(bookingDTO));
    }

    // Test case: Successfully get available delivery slots
    @Test
    void shouldReturnAvailableSlotsSuccessfully() {
        List<DeliverySlot> deliverySlots = List.of(deliverySlot);
        DeliverySlotDTO deliverySlotDTO = new DeliverySlotDTO();
        deliverySlotDTO.setId(1L);

        when(deliverySlotRepository.findByDayAndModeAndMaxReservationsLessThan(any(DayOfWeek.class), any(DeliveryMode.class), anyInt()))
                .thenReturn(deliverySlots);
        when(deliverySlotMapper.toDto(deliverySlots)).thenReturn(List.of(deliverySlotDTO));

        List<DeliverySlotDTO> result = deliveryService.getAvailableSlots(3, "DELIVERY");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(deliverySlotDTO.getId(), result.get(0).getId());
    }

    // Test case: Get available slots - No slots available
    @Test
    void shouldReturnEmptyListWhenNoSlotsAvailable() {
        when(deliverySlotRepository.findByDayAndModeAndMaxReservationsLessThan(any(DayOfWeek.class), any(DeliveryMode.class), anyInt()))
                .thenReturn(List.of());

        List<DeliverySlotDTO> result = deliveryService.getAvailableSlots(3, "DELIVERY");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Test case: Successfully update an delivery's delivery slot and address
    @Test
    void shouldUpdateDeliverySuccessfully() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliverySlotRepository.findById(1L)).thenReturn(Optional.of(deliverySlot));
        when(deliveryRepository.countByDeliverySlotId(1L)).thenReturn(3L); // Below max reservations
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);
        when(deliveryMapper.toDTO(any(Delivery.class))).thenReturn(deliveryDTO);

        DeliveryDTO result = deliveryService.updateDelivery(1L, 1L, "New Address");

        assertNotNull(result);
        assertEquals("New Address", delivery.getDeliveryAddress());
        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    // Test case: Update delivery - delivery not found
    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistentdelivery() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> deliveryService.updateDelivery(1L, 1L, "New Address"));
    }

    // Test case: Update delivery - Delivery slot not found
    @Test
    void shouldThrowNotFoundExceptionWhenDeliverySlotNotFound_updatedelivery() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliverySlotRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> deliveryService.updateDelivery(1L, 2L, "New Address"));
    }

    // Test case: Update delivery - Delivery slot is fully booked
    @Test
    void shouldThrowExceptionWhenDeliverySlotIsFull() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliverySlotRepository.findById(1L)).thenReturn(Optional.of(deliverySlot));
        when(deliveryRepository.countByDeliverySlotId(1L)).thenReturn(5L); // Max reservations reached

        assertThrows(DSMaxReservationException.class, () -> deliveryService.updateDelivery(1L, 1L, "New Address"));
    }

    // Test case: Update delivery - Status is not eligible for update
    @Test
    void shouldThrowExceptionWhendeliveryStatusIsNotEligibleForUpdate() {
        delivery.setStatus(DeliveryStatusEnum.READY); // Not eligible for update
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

        assertThrows(DeliveryNotEligibleToUpdateException.class, () -> deliveryService.updateDelivery(1L, 1L, "New Address"));
    }

    // Test case: Update delivery - delivery not found
    @Test
    void shouldThrowPessimisticLockExceptionWhenDeliveySlotConflict() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliverySlotRepository.findById(1L)).thenThrow(PessimisticLockException.class);
        assertThrows(PessimisticLockException.class, () -> deliveryService.updateDelivery(1L, 1L, "New Address"));
    }
}
