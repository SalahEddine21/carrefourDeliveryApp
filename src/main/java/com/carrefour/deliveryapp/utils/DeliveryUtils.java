package com.carrefour.deliveryapp.utils;

import com.carrefour.deliveryapp.enums.DeliveryStatusEnum;

import java.util.List;

public class DeliveryUtils {

    public static final Integer DELIVERY_MAX_RESERVATION = 5;

    public static List<DeliveryStatusEnum> ELIGIBLE_DELV_STATUS_FOR_UPDATE = List.of(DeliveryStatusEnum.ACCEPTED);
}
