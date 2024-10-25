package ch.floaty.infrastructure;

import ch.floaty.domain.service.AuthenticationExceptions.UserNotFoundException;
import ch.floaty.domain.service.AuthenticationExceptions.WrongPasswordException;
import ch.floaty.domain.service.AuthenticationService;
import ch.floaty.domain.model.SessionToken;
import ch.floaty.domain.model.User;
import ch.floaty.generated.LoginRequestDto;
import ch.floaty.generated.RegisterRequestDto;
import ch.floaty.generated.ResetPasswordRequestDto;
import ch.floaty.generated.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

    public AuthenticationController(AuthenticationService authenticationService, EmailService emailService) {
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

    @PostMapping("/auth/verify-email/{token}")
    public ResponseEntity<String> verifyEmail(@PathVariable String token) {
        authenticationService.verifyEmail(token);
        return ResponseEntity.ok("Email verification successful.");
    }

    @PostMapping("/auth/initiate-password-reset")
    public ResponseEntity<String> initiatePasswordReset(@RequestBody String email) {
        authenticationService.initiatePasswordReset(email);
        return ResponseEntity.ok("A link to reset the password has been send to the email (if it exists).");
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        authenticationService.resetPassword(
                resetPasswordRequestDto.getPasswordResetToken(),
                resetPasswordRequestDto.getNewPassword());
        return ResponseEntity.ok("Password reset successful.");
    }
}
