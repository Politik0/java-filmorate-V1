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
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws DataExistException, ValidationException {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws DataExistException, ValidationException {
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable long id) throws DataExistException {
        return filmService.findFilmById(id);
    }

    @DeleteMapping
    public void removeAllFilms() {
        filmService.removeAllFilms();
    }

    @DeleteMapping("/{id}")
    public void removeFilmById(@PathVariable long id) throws DataExistException {
        filmService.removeFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) throws DataExistException {
        filmService.addLike(id, userId);
    }

    @DeleteMapping ("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) throws DataExistException {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> findTopFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.findTopFilms(count);
    }

}
