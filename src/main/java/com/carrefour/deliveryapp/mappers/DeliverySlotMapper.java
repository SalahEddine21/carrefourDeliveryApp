package com.carrefour.deliveryapp.mappers;

import com.carrefour.deliveryapp.dtos.DeliverySlotDTO;
import com.carrefour.deliveryapp.entities.DeliverySlot;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliverySlotMapper {

    DeliverySlotMapper INSTANCE = Mappers.getMapper(DeliverySlotMapper.class);

    DeliverySlotDTO toDto(DeliverySlot deliverySlot);

    DeliverySlot toEntity(DeliverySlotDTO deliverySlotDTO);

    List<DeliverySlotDTO> toDto(List<DeliverySlot> deliverySlots);
}