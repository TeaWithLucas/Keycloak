package teawithlucas.test.keycloak.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import teawithlucas.test.keycloak.exceptions.DuplicateKeycloakUserException;
import teawithlucas.test.keycloak.exceptions.KeycloakCommunicationException;
import teawithlucas.test.keycloak.exceptions.KeycloakUserCreationException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collections;

/**
 * A service for adding users to Keycloak.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakUserService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realmName;

    /**
     * Creates a new user with the specified username and password in Keycloak.
     *
     * @param username the username of the new user
     * @param password the password of the new user
     * @throws KeycloakUserCreationException if the user creation fails
     * @throws DuplicateKeycloakUserException if a user with the same username already exists in Keycloak
     * @throws KeycloakCommunicationException if there was a problem communicating with the Keycloak server
     */
    public void createUser(String username, String password)
        throws KeycloakUserCreationException {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEnabled(true);
        user.setCredentials(Collections.singletonList(getCredential(password)));
        try (Response response = keycloak.realm(realmName).users().create(user)) {
            int statusCode = response.getStatus();
            switch (statusCode) {
                case 201 -> log.info("User {} successfully created in Keycloak", username);
                case 409 -> {
                    log.error("Duplicate user {}", username);
                    throw new DuplicateKeycloakUserException(username);
                }
                default -> {
                    log.error("Error creating user: status code {}", statusCode);
                    throw new KeycloakCommunicationException("Error creating user: status code " + statusCode);
                }
            }
        } catch (ProcessingException e) {
            log.error("Error creating user in Keycloak", e);
            throw new KeycloakUserCreationException("Error creating user", e);
        }
    }

    private CredentialRepresentation getCredential(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        return credential;
    }
}
