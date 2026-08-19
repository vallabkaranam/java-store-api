package com.vallab.store.controllers;

import com.vallab.store.dtos.CheckoutRequest;
import com.vallab.store.dtos.CheckoutResponse;
import com.vallab.store.dtos.ErrorDto;
import com.vallab.store.entities.Order;
import com.vallab.store.entities.OrderItem;
import com.vallab.store.entities.OrderStatus;
import com.vallab.store.repositories.CartRepository;
import com.vallab.store.repositories.OrderRepository;
import com.vallab.store.services.AuthService;
import com.vallab.store.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> checkout(
        @Valid @RequestBody CheckoutRequest request
    ) {
        var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if (cart == null) {
            return ResponseEntity.badRequest().body(
                new ErrorDto("Cart not found")
            );
        }

        if (cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(
                new ErrorDto("Cart is empty")
            );
        }

        var order = new Order();
        order.setTotalPrice(cart.getTotalPrice());
        order.setStatus(OrderStatus.PENDING);
        order.setCustomer(authService.getCurrentUser());

        cart.getItems().forEach(item -> {
            var orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalPrice(item.getTotalPrice());
            orderItem.setUnitPrice(item.getProduct().getPrice());
            order.getItems().add(orderItem);
        });

        orderRepository.save(order);

        cartService.clearCart(cart.getId());

        return ResponseEntity.ok(new CheckoutResponse(order.getId()));
    }
}
