package com.vallab.store.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CartItemDto {
    private CartProductDto product;
    private int quantity;
    private BigDecimal totalPrice;
}

