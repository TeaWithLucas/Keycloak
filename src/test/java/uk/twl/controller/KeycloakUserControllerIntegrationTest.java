package uk.twl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.twl.model.KeycloakUserDto;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class KeycloakUserControllerIntegrationTest {

    @Autowired
    private transient MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private Keycloak keycloak;

    @Mock
    private RealmResource realmResource;
    @Mock
    private UsersResource usersResource;

    private KeycloakUserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = KeycloakUserDto.builder()
            .username("testuser")
            .password("testpassword")
            .build();

        given(keycloak.realm(anyString())).willReturn(realmResource);
        given(realmResource.users()).willReturn(usersResource);
    }

    @Test
    @DisplayName("Create user should return 201 when user is created")
    void createUserShouldReturn201WhenUserIsCreated() throws Exception {
        // Given
        given(usersResource.create(any(UserRepresentation.class))).willReturn(Response.status(CREATED).build());

        // When
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
            .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(CREATED.getStatusCode());
    }

    @Test
    @DisplayName("Create user should return 409 when user already exists")
    void createUserShouldReturn409WhenUserAlreadyExists() throws Exception {
        // Given
        given(usersResource.create(any(UserRepresentation.class))).willReturn(Response.status(CONFLICT).build());

        // When
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
            .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(CONFLICT.getStatusCode());
    }

    @Test
    @DisplayName("Create user should return 500 when there is a problem communicating with Keycloak")
    void createUserShouldReturn500WhenThereIsAProblemCommunicatingWithKeycloak() throws Exception {
        // Given
        given(usersResource.create(any(UserRepresentation.class))).willReturn(Response.status(INTERNAL_SERVER_ERROR).build());

        // When
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
            .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());
    }
}
