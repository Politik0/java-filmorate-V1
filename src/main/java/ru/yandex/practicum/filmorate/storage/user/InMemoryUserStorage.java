package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public User addUser(User user) throws DataExistException, ValidationException {
        if (users.containsKey(user.getId())) {
            log.debug("Попытка создать юзера, который уже существует");
            throw new DataExistException("Такой пользователь уже существует.");
        } else {
            user.setFriends(new TreeSet<>());
            user.setId(id);
            id++;
            users.put(user.getId(), user);
            log.debug("Новый пользователь {} добавлен. Всего их: " + users.size(), user.getLogin());
            log.debug("Пользователь " + user);
        }
        return users.get(user.getId());
    }

    @Override
    public User updateUser(User user) throws DataExistException, ValidationException {
        if (!users.containsKey(user.getId())) {
            log.debug("Попытка обновить несуществующего пользователя");
            throw new DataExistException("Такой пользователь не существует.");
        } else {
            user.setFriends(users.get(user.getId()).getFriends());
            users.put(user.getId(), user);
            log.debug("Данные пользователя {} обновлены", user.getLogin());
            log.debug("Пользователь " + user);
        }
        return users.get(user.getId());
    }
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) throws DataExistException {
        if (!users.containsKey(id)) {
            log.debug("Попытка получить пользователя по несуществуемому ID.");
            throw new DataExistException("Пользователь с ID " + id + " не существует.");
        }
        return users.get(id);
    }

    @Override
    public void removeUserById(long id) throws DataExistException {
        if (!users.containsKey(id)) {
            log.debug("Попытка удалить пользователя с несуществуеющим ID.");
            throw new DataExistException("Пользователь с ID " + id + " не существует.");
        }
        users.remove(id);
    }

    @Override
    public void addFriend(long userId, long friendId) throws DataExistException {
        User user = users.get(userId);
        User friend = users.get(friendId);
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

    @Override
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

    @Override
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

    @Override
    public void removeFriend(long userId, long friendId) throws DataExistException {
        User user = users.get(userId);
        User friend = users.get(friendId);
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
}
