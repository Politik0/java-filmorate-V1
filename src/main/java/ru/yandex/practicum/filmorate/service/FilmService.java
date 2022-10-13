package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;


    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) throws ValidationException, DataExistException {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws DataExistException, ValidationException {
        return filmStorage.updateFilm(film);
    }

    public void removeAllFilms() {
        filmStorage.removeAllFilms();
    }

    public void removeFilmById(long id) throws DataExistException {
        filmStorage.removeFilmById(id);
    }

    public void addLike(Long filmId, Long userId) throws DataExistException {
        Film film = findFilmById(filmId);
        User user = userStorage.findUserById(userId);
        if (film.getLikes() != null) {
            if (film.getLikes().contains(user.getId())) {
                log.debug("Попытка поставить второй лайк пользователем c ID " + user.getId() + " фильму " +
                        "с ID " + film.getId() + ".");
                throw new DataExistException("Пользователь с ID: " + user.getId() + " уже ставил лайк фильму с ID: "
                        + film.getId());
            }
        }
        film.addLike(user.getId());
        log.debug("Пользователь с ID: " + user.getId() + " поставил like фильму с ID: " + film.getId()
                + ". Количество лайков: " + film.getLikes().size());
    }

    public void removeLike(Long filmId, Long userId) throws DataExistException {
        Film film = findFilmById(filmId);
        User user = userStorage.findUserById(userId);
        if (film.getLikes() != null) {
            if (!film.getLikes().contains(user.getId())) {
                log.debug("Попытка удалить лайк пользователем c ID " + user.getId() + ", который еще не ставил лайк фильму " +
                        "с ID " + film.getId() + ".");
                throw new DataExistException("Пользователь с ID: " + user.getId() + " еще не ставил лайк фильму с ID: "
                        + film.getId());
            }
        }
        film.removeLike(user.getId());
        log.debug("Пользователь с ID: " + user.getId() + " убрал свой like фильму с ID: " + filmId
                + ". Количество лайков: " + film.getLikes().size());
    }

    public List<Film> findTopFilms(int count) {
        return filmStorage.findAllFilms().stream()
                .sorted((x1, x2) ->  x2.getLikesCount() - x1.getLikesCount())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film findFilmById(long id) throws DataExistException {
        return filmStorage.findFilmById(id);
    }
}
