package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws DataExistException, ValidationException {
        log.debug("Получен запрос на добавление пользователя с ID {}.", user.getId());
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws DataExistException, ValidationException {
        log.debug("Получен запрос на обновление пользователя с ID {}.", user.getId());
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.debug("Получен запрос на получение списка всех пользователей.");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable(required = false) long id) throws DataExistException {
        log.debug("Получен запрос на получение пользователя с ID {}.", id);
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void removeUserById(@PathVariable long id) throws DataExistException {
        log.debug("Получен запрос на удаление пользователя с ID {}.", id);
        userService.removeUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable long id, @PathVariable long friendId) throws DataExistException {
        log.debug("Получен запрос на добавление в друзья пользователю с ID {} друга с ID {}.", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) throws DataExistException {
        log.debug("Получен запрос на удаление из друзей у пользователя с ID {} друга с ID {}.", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable long id) throws DataExistException {
        log.debug("Получен запрос на получение всех друзей пользователя с ID {}.", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) throws DataExistException {
        log.debug("Получен запрос на получение общих друзей пользователей с ID {} и с ID {}.", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

}
