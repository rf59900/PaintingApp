package com.ryan_frederick.painting.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private PasswordEncoder passwordEncoder;


    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @GetMapping("")
    @ResponseStatus(code = HttpStatus.OK)
    List<User> getAllUsers() {
        return userRepository.findAllUsers();
    }

    @GetMapping("/id/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    Optional<User> getUserById(@PathVariable("id") Integer id) {
        return userRepository.findUserById(id);
    }

    @GetMapping("/username/{username}")
    @ResponseStatus(code = HttpStatus.OK)
    Optional<User> getUserByUsername(@PathVariable("username") String username) {
        return userRepository.findUserByUsername(username);
    }

    @GetMapping("/test")
    String test(Authentication authentication) {
        return authentication.getAuthorities().toString();
    }

    @PostMapping("")
    @ResponseStatus(code = HttpStatus.CREATED)
    void createUser(@RequestBody CreateUserRequest createUserRequest) {
        String username = createUserRequest.username();
        String password = createUserRequest.password();
        String encodedPassword = passwordEncoder.encode(password);


        // first two created users are assigned admin role
        String userRoles = "ROLE_USER";
        if (userRepository.findAllUsers().size() < 2) {
            userRoles += ",ROLE_ADMIN";
        }

        // average rating is 0 by default
        User user = new User(
                null,
                username,
                encodedPassword,
                LocalDateTime.now(),
                0,
                userRoles,
                null
        );
        // TODO: add pass word hashing and set joined attribute to current time
        userRepository.createUser(user);
    }

    @PatchMapping("")
    @ResponseStatus(code = HttpStatus.OK)
    void updateUserPassword(@RequestBody UserPasswordUpdate toDelete) {
        userRepository.updateUserPassword(toDelete.id(), toDelete.newPassword());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{id}")
    @ResponseStatus(code = HttpStatus.OK)
    void deleteUser(@PathVariable Integer id) {
        userRepository.deleteUserById(id);
    }

}
