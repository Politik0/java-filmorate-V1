package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.*;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException, DataExistException {
        if (getFilmById(film.getId()) != null) {
            log.debug("Попытка создать фильм, который уже существует");
            throw new DataExistException("Такой фильм уже существует.");
        }
        log.debug("Добавляем фильм {}", film.toMap());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_table")
                .usingGeneratedKeyColumns("film_id");
        long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(id);
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("insert into FILM_GENRE(FILM_ID, GENRE_ID)" +
                                "values (?, ?)",
                        film.getId(),
                        genre.getId());
            }
        }
        film.setGenres(genreStorage.getGenresByFilmId(film.getId()));
        log.debug("Фильм {} добавлен, id {}", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws DataExistException, ValidationException {
        if (getFilmById(film.getId()) == null) {
            log.debug("Попытка обновить фильм, который не существует");
            throw new DataExistException("Такой фильм не существует.");
        }
        String sqlQuery = "update FILM_TABLE set name = ?, description = ?, release_date = ?, film_duration = ?, " +
                "mpa_id = ? where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());

        if (film.getGenres() != null) {
            jdbcTemplate.update("delete from FILM_GENRE where FILM_ID = ?", film.getId());
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("insert into FILM_GENRE(FILM_ID, GENRE_ID)" +
                                "values (?, ?)",
                        film.getId(),
                        genre.getId());
            }
        }
        film.setGenres(genreStorage.getGenresByFilmId(film.getId()));
        log.debug("Фильм {} с id {} обновлен", film.getName(), film.getId());
        return film;
    }


    @Override
    public void removeFilmById(long id) throws DataExistException {
        String sqlQuery = "delete from film_table where film_id = ?";
        if (jdbcTemplate.update(sqlQuery, id) > 0) {
            log.info("Фильм с id {} удален.", id);
        } else {
            log.info("Фильм с id {} не найден ", id);
            throw new DataExistException("Такой фильм не существует.");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "select* from film_table";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(Objects.requireNonNull(rs.getDate("release_date")).toLocalDate())
                .duration(rs.getInt("film_duration"))
                .mpa(mpaStorage.getMpaById(rs.getInt("mpa_id")))
                .genres(genreStorage.getGenresByFilmId(rs.getLong("film_id")))
                .likes(new TreeSet<>(getLikes(rs.getLong("film_id"))))
                .build());
    }

    @Override
    public Film getFilmById(long id) {
        String sqlQuery = "select* from film_table where film_id = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRow.next()) {
            log.debug("Найден фильм с id {}.", id);
            return Film.builder()
                    .id(userRow.getLong("film_id"))
                    .name(userRow.getString("name"))
                    .description(userRow.getString("description"))
                    .releaseDate(Objects.requireNonNull(userRow.getDate("release_date")).toLocalDate())
                    .duration(userRow.getInt("film_duration"))
                    .mpa(mpaStorage.getMpaById(userRow.getInt("mpa_id")))
                    .genres(genreStorage.getGenresByFilmId(id))
                    .likes(new TreeSet<>(getLikes(id)))
                    .build();
        }
        return null;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sqlQuery = "insert into film_user (FILM_ID, USER_ID) values (?, ?)";
        int c = jdbcTemplate.update(sqlQuery, filmId, userId);
        if (c > 0) {
            log.debug("Пользователь с ID: " + userId + " поставил like фильму с ID: " + filmId);
        } else {
            log.debug("Лайк уже ставили");
        }
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sqlQuery = "delete from film_user where FILM_ID = ? AND USER_ID = ?";
        int c = jdbcTemplate.update(sqlQuery, filmId, userId);
        if (c > 0) {
            log.debug("Пользователь с ID: " + userId + " убрал like фильму с ID: " + filmId);
        } else {
            log.debug("Лайк еще не ставили");
        }
    }

    private List<Long> getLikes(long id) {
        String sqlQuery = "select USER_ID from film_user where FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> (rs.getLong("user_id")), id);
    }
}
