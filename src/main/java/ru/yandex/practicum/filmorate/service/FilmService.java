package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) throws ValidationException, DataExistException {
        if (notValidate(film)) {
            throw new ValidationException("Валидация не пройдена");
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws DataExistException, ValidationException {
        if (notValidate(film)) {
            throw new ValidationException("Валидация не пройдена");
        }
        return filmStorage.updateFilm(film);
    }

    public void removeFilmById(long id) throws DataExistException {
        filmStorage.removeFilmById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) throws DataExistException {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.info("Фильм с id {} не найден ", id);
            throw new DataExistException("Такой фильм не существует.");
        }
        log.info("Найден фильм с id {} ", id);
        return film;
    }

    public void addLike(Long filmId, Long userId) throws DataExistException {
        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if(film == null || user == null) {
            log.debug("Пользователь с ID {} или фильм с ID {} не существуют.", userId, filmId);
            throw new DataExistException("Такой пользователь или фильм не существуют.");
        }
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) throws DataExistException {
        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if(film == null || user == null) {
            log.debug("Пользователь с ID {} или фильм с ID {} не существуют.", userId, filmId);
            throw new DataExistException("Такой пользователь или фильм не существуют.");
        }
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((x1, x2) ->  x2.getLikesCount() - x1.getLikesCount())
                .limit(count)
                .collect(Collectors.toList());
    }


    private boolean notValidate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            log.debug("Название фильма пустое");
            throw new ValidationException("Название не должно быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.debug("Длина описания больше 200 символов.");
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        }
        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.debug("Дата релиза раньше 28/12/1895");
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
        }
        if (film.getDuration() <= 0) {
            log.debug("Продолжительность фильма меньше или равна нулю");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        if (film.getMpa() == null){
            log.debug("Поле с рейтингом пустое");
            throw new ValidationException("Поле с рейтингом не должно быть пустым.");
        }
        return false;
    }
}
