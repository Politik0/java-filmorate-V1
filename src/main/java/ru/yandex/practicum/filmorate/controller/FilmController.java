package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws DataExistException, ValidationException {
        log.debug("Получен запрос на добавление фильма {}.", film.getName());
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws DataExistException, ValidationException {
        log.debug("Получен запрос на изменение фильма {}.", film.getName());
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.debug("Получен запрос на получение всех фильмов.");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) throws DataExistException {
        log.debug("Получен запрос на получение фильма с ID {}.", id);
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public void removeFilmById(@PathVariable long id) throws DataExistException {
        log.debug("Получен запрос на удаление фильма с ID {}.", id);
        filmService.removeFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) throws DataExistException {
        log.debug("Получен запрос на добавление лайка фильму с ID {} от пользователя с ID {}.", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping ("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) throws DataExistException {
        log.debug("Получен запрос на удаление лайка фильму с ID {} пользователем с ID {}.", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        log.debug("Получен запрос на получение популярных фильмов");
        return filmService.getTopFilms(count);
    }

}
