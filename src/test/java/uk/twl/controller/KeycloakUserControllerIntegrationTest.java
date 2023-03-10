package uk.twl.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.twl.model.KeycloakUserDto;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Keycloak User Controller Integration Tests")
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
    @DisplayName("Create user - valid user DTO provided - returns 201 and calls user service")
    void createUserShouldReturn201WhenUserIsCreated() throws Exception {
        // Given
        given(usersResource.create(any(UserRepresentation.class))).willReturn(Response.status(CREATED).build());

        // When
        MvcResult result = mockMvc.perform(post("/users")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
            .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(CREATED.getStatusCode());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Create user - username is empty/null - returns 400")
    void createUser_ShouldReturnBadRequestStatus_WhenUsernameIsEmpty(String value) throws Exception {
        // Given
        userDto.setUsername(value);

        // When
        MvcResult result = mockMvc.perform(post("/users")
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDto)))
            .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
        assertThat(result.getResponse().getContentType()).isEqualTo(APPLICATION_JSON_VALUE);
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(jsonNode).hasSize(1);
        assertThat(jsonNode.get("username").asText()).isEqualTo("Username is required");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Create user - password is empty/null - returns 400")
    void createUser_ShouldReturnBadRequestStatus_WhenPasswordIsEmpty(String value) throws Exception {
        //Given
        userDto.setPassword(value);

        // When
        MvcResult result = mockMvc.perform(post("/users")
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDto)))
            .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
        assertThat(result.getResponse().getContentType()).isEqualTo(APPLICATION_JSON_VALUE);
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(jsonNode).hasSize(1);
        assertThat(jsonNode.get("password").asText()).isEqualTo("Password is required");
    }

    @Test
    @DisplayName("Create user - user DTO is null - returns 400")
    void createUser_ShouldReturnBadRequestStatus_WhenUserDtoIsNull() throws Exception {
        // When
        MvcResult result = mockMvc.perform(post("/users")
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(null)))
            .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
        assertThat(result.getResponse().getContentType()).contains(TEXT_PLAIN_VALUE);
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Required request body is missing");
    }

    @Test
    @DisplayName("Create user - duplicate user created - returns 409 and throws duplicate user exception")
    void createUserShouldReturn409WhenUserAlreadyExists() throws Exception {
        // Given
        given(usersResource.create(any(UserRepresentation.class))).willReturn(Response.status(CONFLICT).build());

        // When
        MvcResult result = mockMvc.perform(post("/users")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
            .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(CONFLICT.getStatusCode());
    }

    @Test
    @DisplayName("Create user - communication with Keycloak fails - returns 500 and throws Keycloak communication exception")
    void createUserShouldReturn500WhenThereIsAProblemCommunicatingWithKeycloak() throws Exception {
        // Given
        given(usersResource.create(any(UserRepresentation.class))).willReturn(Response.status(INTERNAL_SERVER_ERROR).build());

        // When
        MvcResult result = mockMvc.perform(post("/users")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
            .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());
    }
}
