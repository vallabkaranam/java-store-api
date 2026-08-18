package com.vallab.store.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import com.vallab.store.dtos.UserDto;
import com.vallab.store.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import java.util.List;
import com.vallab.store.mappers.UserMapper;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Sort;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import com.vallab.store.dtos.RegisterUserRequest;
import com.vallab.store.entities.Role;
import org.springframework.web.util.UriComponentsBuilder;
import com.vallab.store.dtos.UpdateUserRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.vallab.store.dtos.ChangePasswordRequest;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users")
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @Operation(summary = "Get all users")
    public List<UserDto> getAllUsers(
        @Parameter(description = "The field to sort by. Allowed values: name, email")
        @RequestParam(required = false, defaultValue = "", name="sort") String sort) {
        if (!Set.of("name", "email").contains(sort)) {
            sort = "name";
        }

        return userRepository.findAll(Sort.by(sort))
            .stream()
            .map(userMapper::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    public ResponseEntity<UserDto> getUser(
        @Parameter(description = "The ID of the user") @PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
            return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping
    @Operation(summary = "Register a new user")
    public ResponseEntity<?> registerUser(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The user registration details")
        @Valid @RequestBody RegisterUserRequest request,
        @Parameter(hidden = true) UriComponentsBuilder uriBuilder) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("email", "Email is already registered."));
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        var userDto = userMapper.toDto(user);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user")
    public ResponseEntity<UserDto> updateUser(
        @Parameter(description = "The ID of the user") @PathVariable(name = "id") Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The updated user details")
        @RequestBody UpdateUserRequest request) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userMapper.update(request, user);
        userRepository.save(user);

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "The ID of the user") @PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    @Operation(summary = "Change a user's password")
    public ResponseEntity<Void> changePassword(
        @Parameter(description = "The ID of the user") @PathVariable(name = "id") Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The current and new password")
        @RequestBody ChangePasswordRequest request) {
        var user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (!user.getPassword().equals(request.getOldPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }
}
