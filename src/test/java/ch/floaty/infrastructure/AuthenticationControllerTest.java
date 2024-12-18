package ch.floaty.infrastructure;

import ch.floaty.domain.model.SessionToken;
import ch.floaty.domain.model.User;
import ch.floaty.domain.service.AuthenticationExceptions.UserNotFoundException;
import ch.floaty.domain.service.AuthenticationExceptions.WrongPasswordException;
import ch.floaty.domain.service.AuthenticationService;
import ch.floaty.generated.LoginRequestDto;
import ch.floaty.generated.RegisterRequestDto;
import ch.floaty.generated.UserDto;
import ch.floaty.run.FloatyApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FloatyApplication.class)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

    private UserDto userDto;
    private User newUser;

    @BeforeEach
    public void setUp() {
        newUser = new User();
        newUser.setId(100L);
        newUser.setName("testuser");
        newUser.setEmail("test@example.com");

        userDto = new UserDto();
        userDto.setId("100");
        userDto.setName("testuser");
    }

    @Test
    public void testRegister_Success() throws Exception {
        // Arrange
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setUsername("testuser");
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password");

        when(authenticationService.register(anyString(), anyString(), anyString()))
                .thenReturn(this.newUser);

        when(modelMapper.map(this.newUser, UserDto.class)).thenReturn(this.userDto);

        // Act + Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/100"))
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.name").value("testuser"));
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setName("testuser");
        loginRequestDto.setPassword("password");
        SessionToken sessionToken = new SessionToken(null);
        sessionToken.setToken("token123");
        when(authenticationService.login(loginRequestDto.getName(), loginRequestDto.getPassword()))
                .thenReturn(sessionToken);

        // Act + Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(cookie().value("sessionToken", "token123"))
                .andExpect(cookie().httpOnly("sessionToken", true));
    }

    @Test
    public void testLogin_UserNotFound() throws Exception {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setName("wronguser");
        loginRequestDto.setPassword("password");
        when(authenticationService.login(anyString(), anyString()))
                .thenThrow(new UserNotFoundException());

        // Act + Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogin_WrongPassword() throws Exception {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setName("testuser");
        loginRequestDto.setPassword("wrongpassword");
        when(authenticationService.login(anyString(), anyString()))
                .thenThrow(new WrongPasswordException());

        // Act + Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Disabled
    //TODO: Fix this test
    public void testLogout_Success() throws Exception {
        // Arrange
        doNothing().when(authenticationService).logout(100L);

        // Act + Assert
        mockMvc.perform(post("/auth/logout/100")  // userId should be part of the URL path
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}