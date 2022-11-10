package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
public class MpaDbStorage implements MpaStorage{
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "select* from MPA";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("name"))
                .build());
    }

    @Override
    public Mpa getMpaById(int id) {
        String sqlQuery = "select* from MPA where MPA_ID = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRow.next()) {
            return Mpa.builder()
                    .id(userRow.getInt("mpa_id"))
                    .name(userRow.getString("name"))
                    .build();
        }
        return null;
    }
}
