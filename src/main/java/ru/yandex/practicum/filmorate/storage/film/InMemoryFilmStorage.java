package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

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
            film.setId(id);
            film.setLikes(new TreeSet<>());
            id++;
            films.put(film.getId(), film);
            log.debug("Фильм {} добавлен. Всего их: " + films.size(), film.getName());
            log.debug("Фильм: " + film);

        }
        return films.get(film.getId());
    }

    @Override
    public Film updateFilm(Film film) throws DataExistException, ValidationException {
        if (!films.containsKey(film.getId())) {
            log.debug("Попытка обновить несуществующий фильм");
            throw new DataExistException("Такой фильм не существует.");
        } else {
            film.setLikes(films.get(film.getId()).getLikes());
            films.put(film.getId(), film);
            log.debug("Фильм c ID {} обновлен", film.getId());
            log.debug("Фильм " + film);
        }
        return films.get(film.getId());
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

    @Override
    public void addLike(Long filmId, Long userId) throws DataExistException {
        Film film = getFilmById(filmId);
        if (film.getLikes() != null) {
            if (film.getLikes().contains(userId)) {
                log.debug("Попытка поставить второй лайк пользователем c ID " + userId + " фильму " +
                        "с ID " + film.getId() + ".");
                throw new DataExistException("Пользователь с ID: " + userId + " уже ставил лайк фильму с ID: "
                        + film.getId());
            }
        }
        film.addLike(userId);
        log.debug("Пользователь с ID: " + userId + " поставил like фильму с ID: " + film.getId()
                + ". Количество лайков: " + film.getLikes().size());
    }

    @Override
    public void removeLike(Long filmId, Long userId) throws DataExistException {
        Film film = getFilmById(filmId);
        if (film.getLikes() != null) {
            if (!film.getLikes().contains(userId)) {
                log.debug("Попытка удалить лайк пользователем c ID " + userId + ", который еще не ставил лайк фильму " +
                        "с ID " + film.getId() + ".");
                throw new DataExistException("Пользователь с ID: " + userId + " еще не ставил лайк фильму с ID: "
                        + film.getId());
            }
        }
        film.removeLike(userId);
        log.debug("Пользователь с ID: " + userId + " убрал свой like фильму с ID: " + filmId
                + ". Количество лайков: " + film.getLikes().size());
    }
}
