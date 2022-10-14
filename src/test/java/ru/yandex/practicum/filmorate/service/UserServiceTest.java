package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    UserService userService;
    UserStorage userStorage;
    User user;
    User friend;

    @BeforeEach
    void beforeEach() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        user = User.builder()
                .id(0)
                .email("email@mail.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        friend = User.builder()
                .id(0)
                .email("friend@mail.ru")
                .login("Friend")
                .name("friendName")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
    }

    @Test
    void addFriend() throws DataExistException {
        userService.addUser(user);
        userService.addUser(friend);
        assertEquals(0, user.getFriends().size(), "Количество друзей неверное.");
        userService.addFriend(user.getId(), friend.getId());
        assertEquals(1, user.getFriends().size(), "Количество друзей неверное.");
        assertTrue(user.getFriends().contains(friend.getId()), "Список друзей не содержит ID друга");
        assertEquals(1, friend.getFriends().size(), "Количество друзей неверное.");

    }

    @Test
    void shouldThrowExceptionWhenDoubleAddFriend() throws DataExistException {
        addFriend();
        DataExistException e = assertThrows(
                DataExistException.class,
                () -> userService.addFriend(user.getId(), friend.getId())
        );
        assertEquals("у пользователя с ID: " + user.getId() + " уже есть друг с ID: "
                + friend.getId(), e.getMessage(), "Не вовзвращается ошибка, когда повторно пытаешься добавить в друзья");
        assertEquals(1, user.getFriends().size(), "Количество друзей неверное.");
        assertEquals(1, friend.getFriends().size(), "Количество друзей неверное.");

    }

    @Test
    void removeFriend() throws DataExistException {
        addFriend();
        userService.removeFriend(user.getId(), friend.getId());
        assertEquals(0, user.getFriends().size(), "Количество друзей неверное.");
        assertEquals(0, friend.getFriends().size(), "Количество друзей неверное.");
    }

    @Test
    void shouldThrowExceptionWhenRemoveNotAddedFriend() throws DataExistException {
        userService.addUser(user);
        userService.addUser(friend);
        assertEquals(0, user.getFriends().size(), "Количество друзей неверное.");
        DataExistException e = assertThrows(
                DataExistException.class,
                () -> userService.removeFriend(user.getId(), friend.getId())
        );
        assertEquals("Пользователь с ID: " + user.getId() + " еще не добавил друга с ID: "
                + friend.getId(), e.getMessage(), "Не возвращается ошибка при поптыке удалить из друзей" +
                "пользователя, который не был добавлен в друзья.");

    }

    @Test
    void findAllFriends() throws DataExistException {
        addFriend();
        User commonFriend = User.builder()
                .id(0)
                .email("CommonFriend@mail.ru")
                .login("CommonFriend")
                .name("CommonFriendName")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        userService.addUser(commonFriend);
        userService.addFriend(user.getId(), commonFriend.getId());
        assertEquals(2, userService.getAllFriends(user.getId()).size(), "Количество друзей неверное.");
        assertTrue(userService.getAllFriends(user.getId()).contains(friend), "Список друзей не содержит друга");

    }

    @Test
    void findCommonFriends() throws DataExistException {
        addFriend();
        User commonFriend = User.builder()
                .id(0)
                .email("CommonFriend@mail.ru")
                .login("CommonFriend")
                .name("CommonFriendName")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        userService.addUser(commonFriend);
        userService.addFriend(user.getId(), commonFriend.getId());
        userService.addFriend(friend.getId(), commonFriend.getId());
        assertEquals(2, user.getFriends().size(), "Количество друзей неверное.");
        assertEquals(2, friend.getFriends().size(), "Количество друзей неверное.");
        assertEquals(3, userService.getCommonFriends(user.getId(), friend.getId()).get(0).getId(),
                "Общий друг возвращается некорректно");
    }
}