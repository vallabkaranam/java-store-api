package com.vallab.store.mappers;

import com.vallab.store.dtos.CartDto;
import com.vallab.store.dtos.CartItemDto;
import com.vallab.store.entities.Cart;
import com.vallab.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDto toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toDto(CartItem cartItem);
}
