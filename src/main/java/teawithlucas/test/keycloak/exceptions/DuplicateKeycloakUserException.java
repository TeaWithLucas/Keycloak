package teawithlucas.test.keycloak.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * An exception that is thrown when a duplicate Keycloak user is detected.
 */
@ResponseStatus(CONFLICT)
public class DuplicateKeycloakUserException extends KeycloakUserCreationException {

    public DuplicateKeycloakUserException(String username) {
        super("Duplicate user: " + username);
    }
}
