package com.carrefour.deliveryapp.mappers;

import com.carrefour.deliveryapp.dtos.DeliveryDTO;
import com.carrefour.deliveryapp.dtos.ProductDTO;
import com.carrefour.deliveryapp.entities.Delivery;
import com.carrefour.deliveryapp.entities.DeliveryProduct;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    DeliveryMapper INSTANCE = Mappers.getMapper(DeliveryMapper.class);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "deliverySlot.id", target = "deliverySlotId")
    @Mapping(target = "products", expression = "java(mapDeliveryProducts(delivery.getDeliveryProducts()))")
    DeliveryDTO toDTO(Delivery delivery);

    default List<ProductDTO> mapDeliveryProducts(List<DeliveryProduct> deliveryProducts) {
        if (deliveryProducts == null) {
            return Collections.emptyList();
        }
        return deliveryProducts.stream()
                .map(dp -> new ProductDTO(dp.getProduct().getId(), dp.getProduct().getCode(), dp.getQuantity()))
                .collect(Collectors.toList());
    }
}
