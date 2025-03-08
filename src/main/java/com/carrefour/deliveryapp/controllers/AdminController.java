package com.carrefour.deliveryapp.controllers;

import com.carrefour.deliveryapp.dtos.DeliveryDTO;
import com.carrefour.deliveryapp.enums.DeliveryStatusEnum;
import com.carrefour.deliveryapp.services.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("authentication.principal.username == 'salahedd.lahmam@gmail.com'") // only for admin users (test case salah)
@Tag(name = "Admin Management", description = "Endpoints for admin operations related to deliveries")
public class AdminController {

    private final DeliveryService deliveryService;
    private final SimpMessagingTemplate messagingTemplate;

    @PatchMapping("/update-delivery-status/{deliveryId}")
    @Operation(
            summary = "Update delivery status",
            description = "Updates the status of a delivery order by its ID. Only accessible to authorized admins."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated delivery status"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    public ResponseEntity<DeliveryDTO> updateDeliveryStatus(
            @Parameter(description = "ID of the delivery to update", required = true)
            @PathVariable Long deliveryId,

            @Parameter(description = "New status for the delivery", required = true)
            @RequestParam DeliveryStatusEnum status
    ) {
        DeliveryDTO updatedDelivery = deliveryService.updateDeliveryStatus(deliveryId, status);
        messagingTemplate.convertAndSend("/topic/delivery-status/" + updatedDelivery.getCustomerId(), updatedDelivery);
        return ResponseEntity.ok(updatedDelivery);
    }
}