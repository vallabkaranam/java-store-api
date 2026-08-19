package com.vallab.store.controllers;

import com.vallab.store.dtos.CheckoutRequest;
import com.vallab.store.dtos.CheckoutResponse;
import com.vallab.store.dtos.ErrorDto;
import com.vallab.store.exceptions.CartEmptyException;
import com.vallab.store.exceptions.CartNotFoundException;
import com.vallab.store.services.CheckoutService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return checkoutService.checkout(request);
    }

    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }
}
