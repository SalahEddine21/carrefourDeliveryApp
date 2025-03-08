package com.carrefour.deliveryapp.enums;

import java.util.Arrays;

public enum DeliveryMode {
    DRIVE,
    DELIVERY,
    DELIVERY_TODAY,
    DELIVERY_ASAP;

    public static DeliveryMode fromString(String mode) {
        return Arrays.stream(DeliveryMode.values())
                .filter(m -> m.name().equalsIgnoreCase(mode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid delivery mode: " + mode));
    }
}
