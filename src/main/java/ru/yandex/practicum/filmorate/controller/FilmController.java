package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws DataExistException, ValidationException {
        if (films.containsKey(film.getId())) {
            log.debug("Попытка создать дублирующий фильм");
            throw new DataExistException("Такой фильм уже есть.");
        } else {
            if (validate(film)) {
                film.setId(id);
                id++;
                films.put(film.getId(), film);
                log.debug("Фильм {} добавлен. Всего их: " + films.size(), film.getName());
            } else {
                throw new ValidationException("Валидация не пройдена");
            }
        }
        return films.get(film.getId());
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws DataExistException, ValidationException {
        if (!films.containsKey(film.getId())) {
            log.debug("Попытка обновить несуществующий фильм");
            throw new DataExistException("Такой фильм не существует.");
        } else {
            if (validate(film)) {
                films.put(film.getId(), film);
                log.debug("Фильм {} обновлен", film.getName());
            } else {
                throw new ValidationException("Валидация не пройдена");
            }
        }
        return films.get(film.getId());
    }

    private boolean validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            log.debug("Название фильма пустое");
            throw new ValidationException("Название не должно быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.debug("Длина описания больше 200 символов");
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
        return true;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

}
