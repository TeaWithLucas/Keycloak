package teawithlucas.test.keycloak.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * An exception that is thrown when there is a communication problem with the Keycloak server.
 */
@ResponseStatus(INTERNAL_SERVER_ERROR)
public class KeycloakCommunicationException extends KeycloakUserCreationException {

    public KeycloakCommunicationException(String message) {
        super(message);
    }

    public KeycloakCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
