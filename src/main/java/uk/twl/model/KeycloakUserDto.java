package uk.twl.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * A DTO class representing a user to be added to Keycloak.
 */
@Data
@Builder
@NotNull
public class KeycloakUserDto {

    @NotBlank(message = "Username is required")
    @Schema(description = "The username of the new user", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "The password of the new user", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
