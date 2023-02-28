package uk.twl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.twl.exception.DuplicateKeycloakUserException;
import uk.twl.exception.KeycloakUserCreationException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("KeycloakUserService tests")
@ExtendWith(MockitoExtension.class)
class KeycloakUserServiceTest {


    @InjectMocks
    private KeycloakUserService userService;
    @Mock
    private Keycloak keycloak;
    @Mock
    private RealmResource realmResource;
    @Mock
    private UsersResource usersResource;
    private String username;
    private String password;

    @BeforeEach
    void setUp() {
        username = "test-user";
        password = "test-password";
        given(keycloak.realm(any())).willReturn(realmResource);
        given(realmResource.users()).willReturn(usersResource);
    }

    @Test
    @DisplayName("should successfully create a new user in Keycloak")
    void shouldSuccessfullyCreateUser() throws KeycloakUserCreationException {
        // Given
        given(usersResource.create(any(UserRepresentation.class))).willReturn(Response.status(201).build());

        // When
        userService.createUser(username, password);

        // Then
        verify(usersResource, times(1)).create(any(UserRepresentation.class));
    }

    @ParameterizedTest
    @EnumSource(
        value = Status.class,
        mode = INCLUDE,
        names = {"BAD_REQUEST", "UNAUTHORIZED", "FORBIDDEN", "NOT_FOUND", "INTERNAL_SERVER_ERROR", "SERVICE_UNAVAILABLE"}
    )
    @DisplayName("should throw KeycloakUserCreationException when response status is not 201 or 409")
    void shouldThrowKeycloakUserCreationException(Status value) {
        // Given
        given(usersResource.create(any(UserRepresentation.class))).willReturn(Response.status(value.getStatusCode()).build());

        // When/Then
        assertThatThrownBy(() -> userService.createUser(username, password))
            .isInstanceOf(KeycloakUserCreationException.class)
            .hasMessageContaining("Error creating user: status code " + value.getStatusCode());
    }

    @Test
    @DisplayName("should throw DuplicateKeycloakUserException when user already exists in Keycloak")
    void shouldThrowDuplicateKeycloakUserException() {
        // Given
        given(usersResource.create(any(UserRepresentation.class))).willReturn(Response.status(CONFLICT.getStatusCode()).build());

        // When/Then
        assertThatThrownBy(() -> userService.createUser(username, password))
            .isInstanceOf(DuplicateKeycloakUserException.class)
            .hasMessageContaining("Duplicate user: " + username);
    }

    @Test
    @DisplayName("should throw KeycloakCommunicationException when there is a problem communicating with Keycloak")
    void shouldThrowKeycloakCommunicationException() {
        // Given
        given(usersResource.create(any(UserRepresentation.class))).willThrow(new ProcessingException("test error"));

        // When/Then
        assertThatThrownBy(() -> userService.createUser(username, password))
            .isInstanceOf(KeycloakUserCreationException.class)
            .hasMessageContaining("Error creating user");
    }
}

