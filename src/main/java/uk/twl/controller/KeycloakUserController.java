package uk.twl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.twl.exception.KeycloakUserCreationException;
import uk.twl.model.KeycloakUserDto;
import uk.twl.service.KeycloakUserService;

/**
 * A controller for managing users in Keycloak.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Keycloak User Management")
@RequiredArgsConstructor
@Slf4j
public class KeycloakUserController {

    private final KeycloakUserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new user to Keycloak", description = "Creates a new user with the specified username and password in Keycloak.")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "409", description = "User already exists")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public void createUser(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The user to add to Keycloak", required = true)
        @RequestBody @Valid KeycloakUserDto user) throws KeycloakUserCreationException {
        userService.createUser(user.getUsername(), user.getPassword());
        log.info("User {} successfully added to Keycloak", user.getUsername());
    }
}
