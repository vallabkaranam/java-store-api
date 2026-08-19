package com.vallab.store.mappers;

import com.vallab.store.dtos.OrderDto;
import com.vallab.store.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
}
