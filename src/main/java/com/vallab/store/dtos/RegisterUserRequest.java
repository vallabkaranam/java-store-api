package com.vallab.store.dtos;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import com.vallab.store.validation.Lowercase;

@Data
public class RegisterUserRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be between 3 and 255 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    @Lowercase(message = "Email must be lowercase")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 25, message = "Password must be between 6 and 25 characters")
    private String password;
}