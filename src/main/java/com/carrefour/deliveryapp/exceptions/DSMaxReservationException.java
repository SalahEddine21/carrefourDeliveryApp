package com.carrefour.deliveryapp.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DSMaxReservationException extends RuntimeException{
    public DSMaxReservationException(String message) {
        super(message);
    }
}
