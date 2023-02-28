package uk.twl.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Keycloak User DTO JSON Tests")
public class KeycloakUserDtoJsonTest {

    public static final String USERNAME = "testuser";
    public static final String PASSWORD = "testpassword";
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JacksonTester<KeycloakUserDto> jacksonTester;

    private KeycloakUserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = KeycloakUserDto.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .build();
    }

    @Test
    @DisplayName("Should serialize KeycloakUserDto to JSON correctly")
    void serializeToJson() throws Exception {
        JsonContent<KeycloakUserDto> json = jacksonTester.write(userDto);

        assertThat(json).extractingJsonPathStringValue("$.username").isEqualTo(USERNAME);
        assertThat(json).extractingJsonPathStringValue("$.password").isEqualTo(PASSWORD);
    }

    @Test
    @DisplayName("Should Deserialize JSON to KeycloakUserDto correctly")
    void deserializeFromJson() throws Exception {
        String json = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", USERNAME, PASSWORD);

        KeycloakUserDto userDto = jacksonTester.parseObject(json);

        assertThat(userDto.getUsername()).isEqualTo(USERNAME);
        assertThat(userDto.getPassword()).isEqualTo(PASSWORD);
    }
}
