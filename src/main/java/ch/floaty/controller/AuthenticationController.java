package ch.floaty.controller;

import ch.floaty.domain.AuthenticationExceptions;
import ch.floaty.domain.AuthenticationExceptions.UserNotFoundException;
import ch.floaty.domain.AuthenticationExceptions.WrongPasswordException;
import ch.floaty.domain.AuthenticationService;
import ch.floaty.domain.SessionToken;
import ch.floaty.domain.User;
import ch.floaty.generated.LoginRequestDto;
import ch.floaty.generated.RegisterRequestDto;
import ch.floaty.generated.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
public class AuthenticationController {

    AuthenticationService authenticationService;
    ModelMapper modelMapper = new ModelMapper();

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequestDto registerRequestDto) {
        User newUser = authenticationService.register(registerRequestDto.getUsername(), registerRequestDto.getEmail(), registerRequestDto.getPassword());
        URI location = URI.create("/users/" + newUser.getId());
        UserDto responseUserDto = modelMapper.map(newUser, UserDto.class);
        return ResponseEntity.created(location).body(responseUserDto);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        SessionToken sessionToken;
        try {
            sessionToken = authenticationService.login(loginRequestDto.getName(), loginRequestDto.getPassword());
        } catch (UserNotFoundException | WrongPasswordException exception) {
            return ResponseEntity.status(UNAUTHORIZED).body(null);
        }
        Cookie cookie = new Cookie("sessionToken", sessionToken.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");  // all endpoints shall return new session cookie
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }
}
