package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film) throws ValidationException, DataExistException;

    Film updateFilm(Film film) throws DataExistException, ValidationException;

    void removeAllFilms();

    void removeFilmById(long id) throws DataExistException;

    List<Film> findAllFilms();

    Film findFilmById(long id) throws DataExistException;
}
