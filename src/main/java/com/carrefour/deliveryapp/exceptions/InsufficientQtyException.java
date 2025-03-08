package com.carrefour.deliveryapp.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsufficientQtyException extends RuntimeException{
    public InsufficientQtyException(String message) {
        super(message);
    }
}
