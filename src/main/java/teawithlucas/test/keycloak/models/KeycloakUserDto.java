package teawithlucas.test.keycloak.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * A DTO class representing a user to be added to Keycloak.
 */
@Data
public class KeycloakUserDto {

    @NotBlank
    @Schema(description = "The username of the new user", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank
    @Schema(description = "The password of the new user", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
