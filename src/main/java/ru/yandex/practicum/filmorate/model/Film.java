package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;


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
    @Builder.Default
    private Set<Long> likes = new TreeSet<>();

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
}
