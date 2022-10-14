package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) throws DataExistException, ValidationException {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws DataExistException, ValidationException {
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long id) throws DataExistException {
        return userStorage.getUserById(id);
    }

    public void removeAllUsers() {
        userStorage.removeAllUsers();
    }

    public void removeUserById(long id) throws DataExistException {
        userStorage.removeUserById(id);
    }

    public void addFriend(long userId, long friendId) throws DataExistException {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user.getFriends() != null) {
            if (user.getFriends().contains(friend.getId())) {
                log.debug("Попытка повторно добавить друга c ID " + friend.getId() + " в друзья к пользователю " +
                        "с ID " + user.getId() + ".");
                throw new DataExistException("у пользователя с ID: " + user.getId() + " уже есть друг с ID: "
                        + friend.getId());
            }
        }
        user.addFriend(friendId);
        log.debug("Пользователь с ID: " + user.getId() + " добавил в друзья друга с ID: " + friend.getId());
        friend.addFriend(userId);
        log.debug("Пользователь с ID: " + user.getId() + " добавлен в друзья у друга с ID: " + friend.getId());

    }

    public void removeFriend(long userId, long friendId) throws DataExistException {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user.getFriends() != null) {
            if (!user.getFriends().contains(friend.getId())) {
                log.debug("Попытка удалить друга c ID " + friend.getId() + ", который не был добавлен в друзья " +
                        "к пользователю с ID " + user.getId() + ".");
                throw new DataExistException("Пользователь с ID: " + user.getId() + " еще не добавил друга с ID: "
                        + friend.getId());
            }
        }
        user.removeFriend(friendId);
        log.debug("Пользователь с ID: " + user.getId() + " удалил из друзей друга с ID: " + friend.getId());
        friend.removeFriend(userId);
        log.debug("Пользователь с ID: " + user.getId() + " удален из друзей у друга с ID: " + friend.getId());
    }

    public List<User> getAllFriends(long id) {
        return getAllUsers().stream()
                .filter(x -> {
                    try {
                        return getUserById(id).getFriends().contains(x.getId());
                    } catch (DataExistException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                })
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long id, long otherId) throws DataExistException {
        List<User> commonFriends = new ArrayList<>();
        for (long friendId : getUserById(id).getFriends()) {
            for (long friendId2 : getUserById(otherId).getFriends()) {
                if (friendId == friendId2) {
                    commonFriends.add(getUserById(friendId2));
                }
            }
        }
        return commonFriends;
    }
}
