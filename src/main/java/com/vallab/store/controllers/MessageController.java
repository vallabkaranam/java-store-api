package com.vallab.store.controllers;

import com.vallab.store.entities.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Messages")
public class MessageController {
    @RequestMapping("/hello")
    @Operation(summary = "Get a hello message")
    public Message sayHello() {
        return new Message("Hello, World!");
    }
}
