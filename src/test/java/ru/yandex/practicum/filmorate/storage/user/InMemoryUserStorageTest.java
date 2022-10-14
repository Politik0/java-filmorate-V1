package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

class InMemoryUserStorageTest extends UserStorageTest<InMemoryUserStorage>{

    @BeforeEach
    public void beforeEach() {
        userStorage = new InMemoryUserStorage();
        user = User.builder()
                .id(0)
                .email("email@mail.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
    }
}