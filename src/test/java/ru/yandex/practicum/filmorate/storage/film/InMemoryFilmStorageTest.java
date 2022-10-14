package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

class InMemoryFilmStorageTest extends FilmStorageTest<InMemoryFilmStorage> {

    @BeforeEach
    public void beforeEach() {
        filmStorage = new InMemoryFilmStorage();
        film = Film.builder()
                .id(0)
                .name("FilName")
                .description("Description1")
                .releaseDate(LocalDate.of(2000, 12,12))
                .duration(90)
                .build();
    }

}