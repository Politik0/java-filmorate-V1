package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws DataExistException, ValidationException {
        if (users.containsKey(user.getId())) {
            log.debug("Попытка создать юзера, который уже существует");
            throw new DataExistException("Такой пользователь уже существует.");
        } else {
             if (validate(user)){
                user.setId(id);
                id++;
                users.put(user.getId(), user);
                log.debug("Новый пользователь {} добавлен. Всего их: " + users.size(), user.getLogin());
            } else {
                 throw new ValidationException("Валидация не пройдена");
             }
        }
        return users.get(user.getId());
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws DataExistException, ValidationException {
        if (!users.containsKey(user.getId())) {
            log.debug("Попытка обновить несуществующего пользователя");
            throw new DataExistException("Такой пользователь не существует.");
        } else {
            if(validate(user)) {
                users.put(user.getId(), user);
                log.debug("Данные пользователя {} обновлены", user.getLogin());
            } else {
                throw new ValidationException("Валидация не пройдена");
            }
        }
        return users.get(user.getId());
    }

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private boolean validate(User user) throws ValidationException {
        if (user.getEmail() == null || !user.getEmail().contains("@") || user.getEmail().isBlank()) {
            log.debug("Эл.почта пустая или не содержит @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        } else if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.debug("Логин пустой или содержит пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Попытка задать дату рождения будущей датой");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.debug("Имя пользователя пустое, для имени использовался логин");
            user.setName(user.getLogin());
        }
        return true;
    }
}
