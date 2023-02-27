package uk.twl.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * An exception that is thrown when a Keycloak user creation operation fails.
 */
@ResponseStatus(INTERNAL_SERVER_ERROR)
public class KeycloakUserCreationException extends Exception {

    public KeycloakUserCreationException(String message) {
        super(message);
    }

    public KeycloakUserCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
