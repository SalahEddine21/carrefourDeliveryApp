package com.carrefour.deliveryapp.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryNotEligibleToUpdateException extends RuntimeException{
    public DeliveryNotEligibleToUpdateException(String message) {
        super(message);
    }
}
