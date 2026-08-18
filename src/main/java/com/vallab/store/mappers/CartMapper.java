package com.vallab.store.mappers;

import com.vallab.store.dtos.CartDto;
import com.vallab.store.entities.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDto toDto(Cart cart);
}
