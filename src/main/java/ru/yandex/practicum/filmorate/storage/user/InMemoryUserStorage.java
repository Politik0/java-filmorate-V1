package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

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
            if (validate(user)){
                user.setFriends(new TreeSet<>());
                user.setId(id);
                id++;
                users.put(user.getId(), user);
                log.debug("Новый пользователь {} добавлен. Всего их: " + users.size(), user.getLogin());
                log.debug("Пользователь " + user);
            } else {
                throw new ValidationException("Валидация не пройдена");
            }
        }
        return users.get(user.getId());
    }

    @Override
    public User updateUser(User user) throws DataExistException, ValidationException {
        if (!users.containsKey(user.getId())) {
            log.debug("Попытка обновить несуществующего пользователя");
            throw new DataExistException("Такой пользователь не существует.");
        } else {
            if(validate(user)) {
                user.setFriends(users.get(user.getId()).getFriends());
                users.put(user.getId(), user);
                log.debug("Данные пользователя {} обновлены", user.getLogin());
                log.debug("Пользователь " + user);
            } else {
                throw new ValidationException("Валидация не пройдена");
            }
        }
        return users.get(user.getId());
    }

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
    public void removeAllUsers() {
        users.clear();
        log.debug("Все пользователи удалены.");
    }

    @Override
    public void removeUserById(long id) throws DataExistException {
        if (!users.containsKey(id)) {
            log.debug("Попытка удалить пользователя с несуществуеющим ID.");
            throw new DataExistException("Пользователь с ID " + id + " не существует.");
        }
        users.remove(id);
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
