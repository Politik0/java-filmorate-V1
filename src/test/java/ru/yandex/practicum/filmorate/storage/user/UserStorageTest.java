package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
abstract class UserStorageTest<T extends UserStorage> {
    protected T userStorage;
    protected User user;

    @Test
    void addNewUser() throws ValidationException, DataExistException {
        userStorage.addUser(user);
        assertEquals(1, userStorage.findAllUsers().size(), "Количество пользователей неверное");
    }

    @Test
    void updateUser() throws ValidationException, DataExistException {
        addNewUser();
        assertEquals(1, userStorage.findAllUsers().size(), "Количество пользователей неверное");
        User userUpdated = User.builder()
                .id(user.getId())
                .email("emailUpdated@mail.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        userStorage.updateUser(userUpdated);
        assertEquals(1, userStorage.findAllUsers().size(), "Количество пользователей неверное");
        assertEquals("emailUpdated@mail.ru", userStorage.findAllUsers().get(0).getEmail(),
                "Неверная почта после обновления");
    }

    @Test
    void shouldThrowExceptionWhenEmailEmpty() {
        user.setEmail("");
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exp.getMessage(),
                "Валидация по почте не прошла");
        assertEquals(0, userStorage.findAllUsers().size(), "Количество пользователей неверное");
    }

    @Test
    void shouldThrowExceptionWhenEmailBlank() {
        user.setEmail(" ");
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exp.getMessage(),
                "Валидация по почте не прошла");
        assertEquals(0, userStorage.findAllUsers().size(), "Количество пользователей неверное");
    }

    @Test
    void shouldThrowExceptionWhenEmailWithoutAtSign() {
        user.setEmail("mail.ru");
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exp.getMessage(),
                "Валидация по почте не прошла");
        assertEquals(0, userStorage.findAllUsers().size(), "Количество пользователей неверное");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndEmailEmpty() throws ValidationException, DataExistException {
        addNewUser();
        User userUpdated = User.builder()
                .id(user.getId())
                .email("")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> userStorage.updateUser(userUpdated)
        );
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exp.getMessage(),
                "Валидация по почте не прошла");
        assertEquals(1, userStorage.findAllUsers().size(), "Количество пользователей неверное");
        assertEquals("email@mail.ru", userStorage.findAllUsers().get(0).getEmail(),
                "Почта после обновления неверная");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndEmailBlank() throws ValidationException, DataExistException {
        addNewUser();
        User userUpdated = User.builder()
                .id(user.getId())
                .email(" ")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> userStorage.updateUser(userUpdated)
        );
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exp.getMessage(),
                "Валидация по почте не прошла");
        assertEquals(1, userStorage.findAllUsers().size(), "Количество пользователей неверное");
        assertEquals("email@mail.ru", userStorage.findAllUsers().get(0).getEmail(),
                "Почта после обновления неверная");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndEmailWithoutAtSign() throws ValidationException, DataExistException {
        addNewUser();
        User userUpdated = User.builder()
                .id(user.getId())
                .email("mail.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> userStorage.updateUser(userUpdated)
        );
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exp.getMessage(),
                "Валидация по почте не прошла");
        assertEquals(1, userStorage.findAllUsers().size(), "Количество пользователей неверное");
        assertEquals("email@mail.ru", userStorage.findAllUsers().get(0).getEmail(),
                "Почта после обновления неверная");
    }

    @Test
    void shouldThrowExceptionWhenLoginEmpty() {
        user.setLogin("");
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("Логин не может быть пустым и содержать пробелы", exp.getMessage(),
                "Валидация по почте не прошла");
        assertEquals(0, userStorage.findAllUsers().size(), "Количество пользователей неверное");
    }

    @Test
    void shouldThrowExceptionWhenLoginBlank() {
        user.setLogin(" ");
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("Логин не может быть пустым и содержать пробелы", exp.getMessage(),
                "Валидация по почте не прошла");
        assertEquals(0, userStorage.findAllUsers().size(), "Количество пользователей неверное");
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsBlank() {
        user.setLogin("Log in");
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("Логин не может быть пустым и содержать пробелы", exp.getMessage(),
                "Валидация по почте не прошла");
        assertEquals(0, userStorage.findAllUsers().size(), "Количество пользователей неверное");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndLoginEmpty() throws ValidationException, DataExistException {
        addNewUser();
        User userUpdated = User.builder()
                .id(user.getId())
                .email("mail@mail.ru")
                .login("")
                .name("Name")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> userStorage.updateUser(userUpdated)
        );
        assertEquals("Логин не может быть пустым и содержать пробелы", exp.getMessage(),
                "Валидация по логину не прошла");
        assertEquals(1, userStorage.findAllUsers().size(), "Количество пользователей неверное");
        assertEquals("Login", userStorage.findAllUsers().get(0).getLogin(),
                "Логин после обновления неверная");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndLoginContainBlank() throws ValidationException, DataExistException {
        addNewUser();
        User userUpdated = User.builder()
                .id(user.getId())
                .email("mail@mail.ru")
                .login("Log in")
                .name("Name")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> userStorage.updateUser(userUpdated)
        );
        assertEquals("Логин не может быть пустым и содержать пробелы", exp.getMessage(),
                "Валидация по логину не прошла");
        assertEquals(1, userStorage.findAllUsers().size(), "Количество пользователей неверное");
        assertEquals("Login", userStorage.findAllUsers().get(0).getLogin(),
                "Логин после обновления неверная");
    }

    @Test
    void shouldAddUserWhenNameEmpty() throws ValidationException, DataExistException {
        user.setName("");
        userStorage.addUser(user);
        assertEquals(1, userStorage.findAllUsers().size(), "Количество пользователей неверное");
        assertEquals("Login", userStorage.findAllUsers().get(0).getName(),
                "Не используется логин вместо пустого имени");
    }

    @Test
    void shouldAddUserWhenUpdatingAndNameEmpty() throws ValidationException, DataExistException {
        addNewUser();
        User userUpdated = User.builder()
                .id(user.getId())
                .email("mail@mail.ru")
                .login("Login")
                .name(" ")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        userStorage.updateUser(userUpdated);
        assertEquals(1, userStorage.findAllUsers().size(), "Количество пользователей неверное");
        assertEquals("Login", userStorage.findAllUsers().get(0).getName(),
                "Не используется логин вместо пустого имени");
    }

    @Test
    void shouldThrowExceptionWhenBirthdayInFuture() {
        user.setBirthday(LocalDate.of(2033, 12, 22));
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("Дата рождения не может быть в будущем.", exp.getMessage(),
                "Валидация по дате рождения не прошла");
        assertEquals(0, userStorage.findAllUsers().size(), "Количество пользователей неверное");
    }

    @Test
    void shouldThrowExceptionWhenBirthdayTomorrow() {
        user.setBirthday(LocalDate.now().plusDays(1));
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("Дата рождения не может быть в будущем.", exp.getMessage(),
                "Валидация по дате рождения не прошла");
        assertEquals(0, userStorage.findAllUsers().size(), "Количество пользователей неверное");
    }

    @Test
    void shouldAddUserWhenBirthdayYesterday() throws ValidationException, DataExistException {
        user.setBirthday(LocalDate.now().minusDays(1));
        userStorage.addUser(user);
        assertEquals(1, userStorage.findAllUsers().size(), "Количество пользователей неверное");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndBirthdayInFuture() throws ValidationException, DataExistException {
        addNewUser();
        User userUpdated = User.builder()
                .id(user.getId())
                .email("mail@mail.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(2033, 12, 22))
                .build();

        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> userStorage.updateUser(userUpdated)
        );
        assertEquals("Дата рождения не может быть в будущем.", exp.getMessage(),
                "Валидация по дате рождения не прошла");
        assertEquals(1, userStorage.findAllUsers().size(), "Количество пользователей неверное");
        assertEquals(LocalDate.of(2000, 2, 20), userStorage.findAllUsers().get(0).getBirthday(),
                "День рождения после обновления неверная");
    }

}