package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;

@Component
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public User addUser(User user) throws DataExistException, ValidationException {
        if (getUserById(user.getId()) != null) {
            log.debug("Попытка создать юзера, который уже существует");
            throw new DataExistException("Такой пользователь уже существует.");
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user_table")
                .usingGeneratedKeyColumns("user_id");
        long id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(id);
        log.debug("Данные пользователя {} добавлены, id {}", user.getLogin(), user.getId());
        return user;
    }


    @Override
    public User updateUser(User user) throws DataExistException, ValidationException {
        if (getUserById(user.getId()) == null) {
            log.debug("Попытка обновить несуществующего пользователя");
            throw new DataExistException("Такой пользователь не существует.");
        }
        String sqlQuery = "update user_table set name = ?, login = ?, birthday = ?, email = ? where USER_ID = ?";
        jdbcTemplate.update(sqlQuery, user.getName(), user.getLogin(), user.getBirthday(), user.getEmail(),
                user.getId());
        log.debug("Данные пользователя {} с id {} обновлены", user.getLogin(), user.getId());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "select* from user_table";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> User.builder()
                        .id(rs.getLong("user_id"))
                .name(rs.getString("name"))
                .login(rs.getString("login"))
                .birthday(Objects.requireNonNull(rs.getDate("birthday")).toLocalDate())
                .email(rs.getString("email"))
                .build());
    }

    @Override
    public User getUserById(long id) throws DataExistException {
        String sqlQuery = "select* from user_table where user_id = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRow.next()) {
            return User.builder()
                    .id(userRow.getLong("user_id"))
                    .name(userRow.getString("name"))
                    .login(userRow.getString("login"))
                    .birthday(Objects.requireNonNull(userRow.getDate("birthday")).toLocalDate())
                    .email(userRow.getString("email"))
                    .build();
        }
        return null;
    }

    @Override
    public void removeUserById(long id) throws DataExistException {
        String sqlQuery = "delete from user_table where user_id = ?";
        if (jdbcTemplate.update(sqlQuery, id) > 0) {
            log.info("Пользователь с id {} удален.", id);
        } else {
            log.info("Пользователь с id {} не найден ", id);
            throw new DataExistException("Такой пользователь не существует.");
        }
    }

    @Override
    public void addFriend(long userId, long friendId) throws DataExistException {
        if (getUserById(userId) == null || getUserById(friendId) == null) {
            log.info("Пользователь с id {} или друг с id {} не найден ", userId, friendId);
            throw new DataExistException("Такой пользователь или друг не существует.");
        }
        String sqlQuery = "insert into friend_table (USER_ID, FRIEND_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getAllFriends(long id) throws DataExistException {
        if (getUserById(id) == null) {
            log.info("Пользователь с id {} не найден ", id);
            throw new DataExistException("Такой пользователь не существует.");
        }
        String sqlQuery = "select* from USER_TABLE UT RIGHT JOIN FRIEND_TABLE FT on UT.USER_ID = FT.FRIEND_ID " +
                "where FT.USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> User.builder()
                .id(rs.getLong("user_id"))
                .name(rs.getString("name"))
                .login(rs.getString("login"))
                .birthday(Objects.requireNonNull(rs.getDate("birthday")).toLocalDate())
                .email(rs.getString("email"))
                .build(), id);
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        String sqlQuery = "SELECT* FROM user_table WHERE user_id IN (SELECT friend_id FROM friend_table " +
                "WHERE user_id = ? OR user_id = ? GROUP BY friend_id HAVING COUNT(user_id) > 1);";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> User.builder()
                .id(rs.getLong("user_id"))
                .name(rs.getString("name"))
                .login(rs.getString("login"))
                .birthday(Objects.requireNonNull(rs.getDate("birthday")).toLocalDate())
                .email(rs.getString("email"))
                .build(), id, otherId);
    }

    @Override
    public void removeFriend(long userId, long friendId) throws DataExistException {
        String sqlQuery = "delete from friend_table where user_id = ? AND friend_id = ?";
        if (jdbcTemplate.update(sqlQuery, userId, friendId) > 0) {
            log.info("Друг с id {} удален у пользователя с id {}.", friendId, userId);
        } else {
            log.info("Пользователь с id {} или друг с id {} не найден ", friendId, userId);
            throw new DataExistException("Такой пользователь или друг не существует.");
        }
    }
}
