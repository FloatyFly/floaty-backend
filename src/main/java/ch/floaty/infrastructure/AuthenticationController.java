package ch.floaty.infrastructure;

import ch.floaty.domain.repository.IUserRepository;
import ch.floaty.domain.service.AuthenticationExceptions.UserNotFoundException;
import ch.floaty.domain.service.AuthenticationExceptions.WrongPasswordException;
import ch.floaty.domain.service.AuthenticationService;
import ch.floaty.domain.model.SessionToken;
import ch.floaty.domain.model.User;
import ch.floaty.generated.LoginRequestDto;
import ch.floaty.generated.RegisterRequestDto;
import ch.floaty.generated.ResetPasswordRequestDto;
import ch.floaty.generated.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@Slf4j
public class AuthenticationController {

    AuthenticationService authenticationService;
    IUserRepository userRepository;
    ModelMapper modelMapper = new ModelMapper();

    public AuthenticationController(AuthenticationService authenticationService, IUserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequestDto registerRequestDto) {
        User newUser = authenticationService.register(registerRequestDto.getUsername(), registerRequestDto.getEmail(), registerRequestDto.getPassword());
        URI location = URI.create("/users/" + newUser.getId());
        UserDto responseUserDto = modelMapper.map(newUser, UserDto.class);
        log.info("Registered user '{}' and sent email verification link to {}.", registerRequestDto.getUsername(), registerRequestDto.getEmail());
        return ResponseEntity.created(location).body(responseUserDto);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        SessionToken sessionToken;
        try {
            sessionToken = authenticationService.login(loginRequestDto.getUsername(), loginRequestDto.getPassword());
        } catch (UserNotFoundException | WrongPasswordException exception) {
            log.info("Login attempt failed for '{}'. Reason: {} {}", loginRequestDto.getUsername(), exception.getClass().getName(), exception.getMessage());
            return ResponseEntity.status(UNAUTHORIZED).body(null);
        }
        Cookie cookie = new Cookie("sessionToken", sessionToken.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");  // all endpoints shall return new session cookie
        response.addCookie(cookie);
        UserDto responseUserDto = modelMapper.map(this.userRepository.findByName(loginRequestDto.getUsername()), UserDto.class);
        log.info("User '{}' logged in successfully.", loginRequestDto.getUsername());
        return ResponseEntity.ok().body(responseUserDto);
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

    @PostMapping("/auth/logout/{userId}")
    @PreAuthorize("@userSecurity.hasUserIdOrAdmin(#userId)")
    public ResponseEntity<String> logout(@PathVariable Long userId) {
        authenticationService.logout(userId);
        return ResponseEntity.ok("Logout successful.");
    }
}
