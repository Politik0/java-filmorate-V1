package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long id = 1;

    @Override
    public Film addFilm(Film film) throws ValidationException, DataExistException {
        if (films.containsKey(film.getId())) {
            log.debug("Попытка создать дублирующий фильм");
            throw new DataExistException("Такой фильм уже есть.");
        } else {
            if (validate(film)) {
                film.setId(id);
                film.setLikes(new TreeSet<>());
                id++;
                films.put(film.getId(), film);
                log.debug("Фильм {} добавлен. Всего их: " + films.size(), film.getName());
                log.debug("Фильм: " + film);
            } else {
                throw new ValidationException("Валидация не пройдена");
            }
        }
        return films.get(film.getId());
    }

    @Override
    public Film updateFilm(Film film) throws DataExistException, ValidationException {
        if (!films.containsKey(film.getId())) {
            log.debug("Попытка обновить несуществующий фильм");
            throw new DataExistException("Такой фильм не существует.");
        } else {
            if (validate(film)) {
                film.setLikes(films.get(film.getId()).getLikes());
                films.put(film.getId(), film);
                log.debug("Фильм c ID {} обновлен", film.getId());
                log.debug("Фильм " + film);
            } else {
                throw new ValidationException("Валидация не пройдена");
            }
        }
        return films.get(film.getId());
    }

    @Override
    public void removeAllFilms() {
        films.clear();
        log.debug("Все фильмы удалены.");
    }

    @Override
    public void removeFilmById(long id) throws DataExistException {
        if (!films.containsKey(id)) {
            log.debug("Попытка удалить фильм с несуществуеющим ID.");
            throw new DataExistException("Фильма с ID " + id + " не существует.");
        } else {
            films.remove(id);
        }
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(long id) throws DataExistException {
        if (!films.containsKey(id)) {
            log.debug("Попытка получить фильм по несуществуемому ID.");
            throw new DataExistException("Фильма с ID " + id + " не существует.");
        }
        return films.get(id);
    }

    private boolean validate(Film film) throws ValidationException {
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
        return true;
    }
}
