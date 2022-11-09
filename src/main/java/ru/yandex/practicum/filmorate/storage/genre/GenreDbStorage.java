package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "select* from GENRE";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build());
    }

    @Override
    public Genre getGenreById(int id) {
        String sqlQuery = "select distinct * from GENRE where GENRE_ID = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRow.next()) {
            return Genre.builder()
                    .id(userRow.getInt("genre_id"))
                    .name(userRow.getString("name"))
                    .build();
        }
        return null;
    }

    @Override
    public List<Genre> getGenresByFilmId(Long id) {
        String sqlQuery = "select distinct * from GENRE G RIGHT JOIN FILM_GENRE FG on G.GENRE_ID = FG.GENRE_ID WHERE FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build(), id);
    }
}
