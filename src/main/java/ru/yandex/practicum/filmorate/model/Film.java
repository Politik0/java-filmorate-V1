package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;


@Builder
@Data
public class Film {
    private long id;
    private String name;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    private int duration;
    private Mpa mpa;
    private List<Genre> genres;
    @Builder.Default
    private Set<Long> likes = new TreeSet<>();

/*    public Film(long film_id, String title, String description, LocalDate releaseDate, int duration, Mpa mpa) {
        this.film_id = film_id;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }*/

    public void addLike(long id) {
        likes.add(id);
    }

    public void removeLike(long id) {
        likes.remove(id);
    }

    public int getLikesCount() {
        if (likes == null) {
            return 0;
        } else {
            return likes.size();
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("film_duration", duration);
        values.put("mpa_id", mpa.getId());
        values.put("genres", genres);
        return  values;
    }
}
