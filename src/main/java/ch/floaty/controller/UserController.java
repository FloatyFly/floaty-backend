package ch.floaty.controller;

import ch.floaty.domain.User;
import ch.floaty.domain.IUserRepository;
import ch.floaty.generated.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    public List<UserDto> findAllUsers() {
        List<User> users = (List<User>) IUserRepository.findAll();
        return users.stream().map(UserController::toUserDto).collect(toList());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> findUserById(@PathVariable(value = "id") long id) {
        Optional<User> user = IUserRepository.findById(id);
        return user.map(value -> ResponseEntity.ok().body(toUserDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public UserDto saveUser(@Validated @RequestBody UserDto userDto) {
        // TODO (Matthäus): This is quite some logic and should go into an application or even domain service.
        // TODO (Matthäus): The userId should be given by the backend and not by the frontend.
        // TODO (Matthäus): This whole thing will probably go away once we have proper user management.
        Long nextUserId = ((List<User>) IUserRepository.findAll()).
                stream().
                map(User::getId).
                max(Long::compareTo).orElse(0L) + 1;
        User newUser = new User();
        newUser.setName(userDto.getName());
        newUser.setId(nextUserId);
        return toUserDto(IUserRepository.save(newUser));
    }

    @DeleteMapping("/users/{id}")
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

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String root() {
        System.out.println("Calling root path.");
        return "<html>\n" + "<header><title>Welcome</title></header>\n" +
                "<body>\n" +
                "<pre>\n" +
                "\n" +
                "                                                                                                                                                                  ___  \n" +
                "               ___     _                      _       _  _     \n" +
                "              | __|   | |     ___    __ _    | |_    | || |    \n" +
                "              | _|    | |    / _ |  / _` |   |  _|   |_, |    \n" +
                "             _|_|_   _|_|_   |___/  |__,_|   |__|   _|__/     \n" +
                "           _| ''' |_|''''''|_|''''|_|'''''|_|'''''|_| '''''| .'\n" +
                "           '`-0-0-''`-0-0-''`-0-0-''`-0-0-''`-0-0-''`-0-0-' " +
                "</pre>\n" +
                "</body>\n" +
                "</html>";
    }

    private static UserDto toUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
