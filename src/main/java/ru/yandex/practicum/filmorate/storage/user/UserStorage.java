package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user) throws DataExistException, ValidationException;

    User updateUser(User user) throws DataExistException, ValidationException;

    List<User> getAllUsers();

    User getUserById(long id) throws DataExistException;

    void removeUserById(long id) throws DataExistException;
    void addFriend(long userId, long friendId) throws DataExistException;
    List<User> getAllFriends(long id) throws DataExistException;
    List<User> getCommonFriends(long id, long otherId) throws DataExistException;
    void removeFriend(long userId, long friendId) throws DataExistException;
}
