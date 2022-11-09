package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
        public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) throws DataExistException, ValidationException {
        if(!validate(user)) {
            throw new ValidationException("Валидация не пройдена");
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws DataExistException, ValidationException {
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long id) throws DataExistException {
        log.info("Поиск пользователя с id {} ", id);
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.info("Пользователь с id {} не найден ", id);
            throw new DataExistException("Такой пользователь не существует.");
        }
        log.info("Найден пользователь с id {} ", id);
        return user;
    }

    public void removeUserById(long id) throws DataExistException {
        userStorage.removeUserById(id);
    }

    public void addFriend(long userId, long friendId) throws DataExistException {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) throws DataExistException {
       userStorage.removeFriend(userId, friendId);
    }

    public List<User> getAllFriends(long id) throws DataExistException {
        return userStorage.getAllFriends(id);
    }

    public List<User> getCommonFriends(long id, long otherId) throws DataExistException {
        return userStorage.getCommonFriends(id, otherId);
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
