package com.ryan_frederick.painting.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

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

    @PostMapping("")
    @ResponseStatus(code = HttpStatus.CREATED)
    void createUser(@RequestBody User user) {
        // TODO: add pass word hashing and set joined attribute to current time
        userRepository.createUser(user);
    }

    @PatchMapping("")
    @ResponseStatus(code = HttpStatus.OK)
    void updateUserPassword(@RequestBody UserPasswordUpdate toDelete) {
        userRepository.updateUserPassword(toDelete.id(), toDelete.newPassword());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(code = HttpStatus.OK)
    void deleteUser(@PathVariable Integer id) {
        userRepository.deleteUserById(id);
    }

}
