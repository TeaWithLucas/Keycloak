package uk.twl.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.twl.exception.DuplicateKeycloakUserException;
import uk.twl.exception.KeycloakCommunicationException;
import uk.twl.model.KeycloakUserDto;
import uk.twl.service.KeycloakUserService;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("Keycloak User Controller Unit Tests")
class KeycloakUserControllerTest {

    @Mock
    private KeycloakUserService userService;

    @InjectMocks
    private KeycloakUserController controller;
    private KeycloakUserDto user;

    @BeforeEach
    void setUp() {
        user = KeycloakUserDto.builder()
            .username("testuser")
            .password("testpassword")
            .build();
    }

    @Test
    @DisplayName("Create user - success")
    void createUserSuccess() throws Exception {
        // Given
        willDoNothing().given(userService).createUser(user.getUsername(), user.getPassword());

        // When/Then
        assertThatNoException().isThrownBy(() -> controller.createUser(user));
    }

    @Test
    @DisplayName("Create user - user already exists")
    void createUserAlreadyExists() throws Exception {
        // Given

        doThrow(new DuplicateKeycloakUserException(user.getUsername()))
            .when(userService).createUser(user.getUsername(), user.getPassword());

        // When/Then
        assertThatThrownBy(() -> controller.createUser(user))
            .isInstanceOf(DuplicateKeycloakUserException.class)
            .hasMessageContaining("Duplicate user: " + user.getUsername());
    }

    @Test
    @DisplayName("Create user - Keycloak communication error")
    void createUserKeycloakError() throws Exception {
        // Given

        doThrow(new KeycloakCommunicationException("Test Error"))
            .when(userService).createUser(user.getUsername(), user.getPassword());

        // When/Then
        assertThatThrownBy(() -> controller.createUser(user))
            .isInstanceOf(KeycloakCommunicationException.class)
            .hasMessageContaining("Test Error");
    }
}
