package com.carrefour.deliveryapp.controllers;

import com.carrefour.deliveryapp.dtos.BookingDTO;
import com.carrefour.deliveryapp.dtos.DeliveryDTO;
import com.carrefour.deliveryapp.dtos.DeliverySlotDTO;
import com.carrefour.deliveryapp.services.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Deliveries", description = "API for managing deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/book")
    @Operation(
            summary = "Book a delivery",
            description = "Creates a new delivery based on the provided booking details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully booked delivery"),
            @ApiResponse(responseCode = "4XX", description = "Error in booking delivery", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{code: 4XX, message: Error details}")
            ))
    })
    public ResponseEntity<DeliveryDTO> bookDelivery(
            @Valid @RequestBody BookingDTO bookingDTO) {
        DeliveryDTO delivery = deliveryService.bookDelivery(bookingDTO);
        messagingTemplate.convertAndSend("/topic/delivery-slots", delivery);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping("/state/{deliveryId}")
    @Operation(summary = "Get delivery updates", description = "Retrieves the current state of a delivery.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully booked delivery"),
            @ApiResponse(responseCode = "404", description = "Delivery not found",  content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{code: 404, message: Delivery not found}")
            ))
    })
    public ResponseEntity<String> getDeliveryUpdates(
            @Parameter(description = "Delivery ID", example = "1")
            @PathVariable Long deliveryId) {
        String deliveryStatus = deliveryService.getDeliveryCurrentStatus(deliveryId);
        return ResponseEntity.ok(deliveryStatus);
    }

    @PatchMapping("/modify/{deliveryId}")
    @Operation(summary = "Modify a delivery", description = "Updates the delivery slot or address.")
    public ResponseEntity<DeliveryDTO> modifyDelivery(
            @NotNull
            @Parameter(description = "Delivery ID", example = "1")
            @PathVariable Long deliveryId,
            @Parameter(description = "New slot ID", example = "2")
            @RequestParam(required = false) Long newSlotId,
            @Parameter(description = "New delivery address", example = "123 Main St")
            @RequestParam(required = false) String newAddress
    ) {
        DeliveryDTO updateDelivery = deliveryService.updateDelivery(deliveryId, newSlotId, newAddress);
        messagingTemplate.convertAndSend("/topic/delivery-status/" + updateDelivery.getCustomerId(), updateDelivery);
        return ResponseEntity.ok(updateDelivery);
    }

    public ResponseEntity<DeliveryDTO> updateDelivery(
        @Valid @RequestBody DeliveryDTO deliveryDTO
    ) {
        DeliveryDTO updateDelivery = deliveryService.updateDelivery(deliveryDTO.getId(),
                deliveryDTO.getDeliverySlotId(), deliveryDTO.getDeliveryAddress());
        messagingTemplate.convertAndSend("/topic/delivery-status/" + updateDelivery.getCustomerId(), updateDelivery);
        return ResponseEntity.ok(updateDelivery);
    }

    @GetMapping("/available/{dayIndex}/{mode}")
    @Operation(summary = "Get available delivery slots", description = "Retrieves available slots for a given day and mode.")
    public ResponseEntity<List<DeliverySlotDTO>> getAvailableSlots(
            @Parameter(description = "Day index", example = "1") @PathVariable Integer dayIndex,
            @Parameter(description = "Delivery mode", example = "DELIVERY") @PathVariable String mode) {
        List<DeliverySlotDTO> slots = deliveryService.getAvailableSlots(dayIndex, mode);
        return ResponseEntity.ok(slots);
    }
}