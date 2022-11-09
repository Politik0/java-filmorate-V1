package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

class InMemoryFilmStorageTest extends FilmStorageTest<InMemoryFilmStorage> {

    @BeforeEach
    public void beforeEach() {
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, new InMemoryUserStorage());
        film = Film.builder()
                .id(0)
                .name("FilName")
                .description("Description1")
                .releaseDate(LocalDate.of(2000, 12,12))
                .duration(90)
                .mpa(Mpa.builder().id(1).build())
                .build();
    }

}