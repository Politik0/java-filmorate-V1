package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user) throws DataExistException, ValidationException;

    User updateUser(User user) throws DataExistException, ValidationException;

    List<User> getAllUsers();

    User getUserById(long id) throws DataExistException;

    void removeAllUsers();

    void removeUserById(long id) throws DataExistException;
}
