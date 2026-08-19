package com.vallab.store.controllers;

import com.vallab.store.dtos.AddItemToCartRequest;
import com.vallab.store.dtos.CartDto;
import com.vallab.store.dtos.CartItemDto;
import com.vallab.store.dtos.ErrorDto;
import com.vallab.store.dtos.UpdateCartItemRequest;
import com.vallab.store.exceptions.CartNotFoundException;
import com.vallab.store.exceptions.ProductNotFoundException;
import com.vallab.store.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/carts")
@Tag(name = "Carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    @Operation(summary = "Create a new cart")
    public ResponseEntity<CartDto> createCart(
        @Parameter(hidden = true) UriComponentsBuilder uriBuilder
    ) {
        var cartDto = cartService.createCart();
        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();

        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("/{cartId}/items")
    @Operation(summary = "Add a product to a cart, or increment quantity by 1 if it is already in the cart")
    public ResponseEntity<CartItemDto> addToCart(
        @Parameter(description = "The ID of the cart") @PathVariable UUID cartId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The product to add to the cart")
        @Valid @RequestBody AddItemToCartRequest request) {
        var cartItemDto = cartService.addToCart(cartId, request.getProductId());

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }

    @GetMapping("/{cartId}")
    @Operation(summary = "Get a cart by ID")
    public CartDto getCart(
        @Parameter(description = "The ID of the cart") @PathVariable UUID cartId
    ) {
        return cartService.getCart(cartId);
    }

    @PutMapping("/{cartId}/items/{productId}")
    @Operation(summary = "Update the quantity of a cart item")
    public CartItemDto updateItem(
        @Parameter(description = "The ID of the cart") @PathVariable("cartId") UUID cartId,
        @Parameter(description = "The ID of the product") @PathVariable("productId") Long productId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The new quantity to set on the cart item")
        @Valid @RequestBody UpdateCartItemRequest request
    ) {
       return cartService.updateItem(cartId, productId, request.getQuantity());
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    @Operation(summary = "Remove a product from a cart")
    public ResponseEntity<?> removeItem(
        @Parameter(description = "The ID of the cart") @PathVariable("cartId") UUID cartId,
        @Parameter(description = "The ID of the product") @PathVariable("productId") Long productId
    ) {
        cartService.removeItem(cartId, productId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    @Operation(summary = "Clear all items from a cart")
    public ResponseEntity<Void> clearCart(
        @Parameter(description = "The ID of the cart") @PathVariable UUID cartId
    ) {
        cartService.clearCart(cartId);

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCartNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto("Cart not found."));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDto> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto("Product not found."));
    }
}
