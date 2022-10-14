package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmServiceTest {
    FilmService filmService;
    FilmStorage filmStorage;
    UserStorage userStorage;
    Film film;
    User user;

    @BeforeEach
    void beforeEach() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
        film = Film.builder()
                .id(0)
                .name("FilName")
                .description("Description1")
                .releaseDate(LocalDate.of(2000, 12,12))
                .duration(90)
                .build();
        user = User.builder()
                .id(0)
                .email("email@mail.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
    }

    @Test
    void addLike() throws DataExistException {
        assertEquals(0, filmService.getAllFilms().size(), "Количество фильмов неверное");
        filmStorage.addFilm(film);
        userStorage.addUser(user);
        assertEquals(1, filmService.getAllFilms().size(), "Количество фильмов неверное");
        assertEquals(0, film.getLikesCount(), "Количество лайков неверное");
        filmService.addLike(film.getId(), user.getId());
        assertEquals(1, film.getLikesCount(), "Количество лайков неверное");
    }

    @Test
    void shouldThrowExceptionWhenAddDoubleLike() throws DataExistException {
        assertEquals(0, filmService.getAllFilms().size(), "Количество фильмов неверное");
        addLike();
        assertEquals(1, filmService.getAllFilms().size(), "Количество фильмов неверное");
        DataExistException e = assertThrows(
                DataExistException.class,
                () -> filmService.addLike(film.getId(), user.getId())
        );
        assertEquals("Пользователь с ID: " + user.getId() + " уже ставил лайк фильму с ID: "
                + film.getId(), e.getMessage(), "Нет ошибки при повторном лайке");
        assertEquals(1, film.getLikesCount(), "Количество лайков неверное");

    }

    @Test
    void removeLike() throws DataExistException {
        addLike();
        assertEquals(1, filmService.getAllFilms().size(), "Количество фильмов неверное");
        assertEquals(1, film.getLikesCount(), "Количество лайков неверное");
        filmService.removeLike(film.getId(), user.getId());
        assertEquals(0, film.getLikesCount(), "Количество лайков неверное");

    }

    @Test
    void shouldThrowExceptionWhenNoLikesAndRemoveLike() throws DataExistException {
        filmStorage.addFilm(film);
        userStorage.addUser(user);
        assertEquals(1, filmService.getAllFilms().size(), "Количество фильмов неверное");
        DataExistException e = assertThrows(
                DataExistException.class,
                () -> filmService.removeLike(film.getId(), user.getId())
        );
        assertEquals("Пользователь с ID: " + user.getId() + " еще не ставил лайк фильму с ID: "
                + film.getId(), e.getMessage(), "Нет ошибки при попытке удалить лайк, который не был поставлен");
        assertEquals(0, film.getLikesCount(), "Количество лайков неверное");
    }

    @Test
    void findTopFilms() throws DataExistException {
        addLike();
        assertEquals(1, filmService.getAllFilms().size(), "Количество фильмов неверное");
        Film film2 = Film.builder()
                .id(0)
                .name("FilName2")
                .description("Description2")
                .releaseDate(LocalDate.of(2000, 12,12))
                .duration(90)
                .build();
        filmStorage.addFilm(film2);
        assertEquals(2, filmService.getAllFilms().size(), "Количество фильмов неверное");
        assertEquals(1, film.getLikesCount(), "Количество лайков неверное");
        assertEquals(0, film2.getLikesCount(), "Количество лайков неверное");
        assertEquals(2, filmService.getAllFilms().size(), "Количество фильмов неверное");
        assertEquals(2,filmService.getTopFilms(10).size(), "Топ фильмов формируется не корректно");
        assertEquals(1, filmService.getTopFilms(10).get(0).getId(), "Топ фильмов формируется не корректно");
    }



}