package ch.floaty.infrastructure;

import ch.floaty.domain.model.User;
import ch.floaty.domain.repository.IUserRepository;
import ch.floaty.generated.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.*;
import static java.lang.Boolean.FALSE;
import static java.util.stream.Collectors.toList;

@RestController
public class UserController {
    private final IUserRepository IUserRepository;
    private static final ModelMapper modelMapper = new ModelMapper();
    public UserController(IUserRepository IUserRepository) {
        this.IUserRepository = IUserRepository;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDto> findAllUsers() {
        List<User> users = (List<User>) IUserRepository.findAll();
        return users.stream().map(UserController::toUserDto).collect(toList());
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("@userSecurity.hasUserIdOrAdmin(#userId)")
    public ResponseEntity<UserDto> findUserById(@PathVariable Long userId) {
        Optional<User> user = IUserRepository.findById(userId);
        return user.map(value -> ResponseEntity.ok().body(toUserDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Map<String, Boolean> deleteUserById(@PathVariable long id) {
        Optional<User> user = IUserRepository.findById(id);

        Map<String, Boolean> response = new HashMap<>();

        if (user.isPresent()) {
            IUserRepository.delete(user.get());
            response.put("deleted", TRUE);
        } else {
            response.put("deleted", FALSE);
        }

        return response;
    }

    private static UserDto toUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
