package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
abstract class FilmStorageTest<T extends FilmStorage> {
    protected T filmStorage;
    protected FilmService filmService;
    protected Film film;

    @Test
    void addNewFilm() throws ValidationException, DataExistException {
        filmService.addFilm(film);
        assertEquals(1, filmService.getAllFilms().size());
    }

    @Test
    void updateFilm() throws ValidationException, DataExistException {
        filmService.addFilm(film);
        Film filmUpdated = Film.builder()
                .id(film.getId())
                .name("Updated Name")
                .description("Description1")
                .releaseDate(LocalDate.of(2000, 12,12))
                .duration(90)
                .mpa(Mpa.builder().id(1).build())
                .build();
        filmService.updateFilm(filmUpdated);
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals("Updated Name", filmService.getAllFilms().get(0).getName(), "Название после" +
                "обновления не верно" );

    }

    @Test
    void shouldThrowExceptionWhenEmptyFilmName() {
        film.setName("");
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(film)
        );
        assertEquals("Название не должно быть пустым.", exp.getMessage(), "Валидация по пустому имени не прошла");
        assertEquals(0, filmService.getAllFilms().size());
    }

    @Test
    void shouldThrowExceptionWhenBlankFilmName() {
        film.setName(" ");
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(film)
        );
        assertEquals("Название не должно быть пустым.", exp.getMessage(), "Валидация по пустому " +
                "имени не прошла");
        assertEquals(0, filmService.getAllFilms().size());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithEmptyFilmName() throws ValidationException, DataExistException {
        addNewFilm();
        assertEquals(1, filmService.getAllFilms().size());
        Film filmUpdated = Film.builder()
                .id(film.getId())
                .name("")
                .description("Description1")
                .releaseDate(LocalDate.of(2000, 12,12))
                .duration(90)
                .build();

        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> filmService.updateFilm(filmUpdated)
        );
        assertEquals("Название не должно быть пустым.", exp.getMessage(), "Валидация по пустому " +
                "имени не прошла");
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals(film.getName(), "FilName", "Название фильма после обновления неверное");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithBlankFilmName() throws ValidationException, DataExistException {
        addNewFilm();
        assertEquals(1, filmService.getAllFilms().size());
        Film filmUpdated = Film.builder()
                .id(film.getId())
                .name(" ")
                .description("Description1")
                .releaseDate(LocalDate.of(2000, 12,12))
                .duration(90)
                .build();

        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> filmService.updateFilm(filmUpdated)
        );
        assertEquals("Название не должно быть пустым.", exp.getMessage(), "Валидация по пустому имени не прошла");
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals(filmService.getAllFilms().get(0).getName(), "FilName", "Название фильма после обновления неверное");
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsMoreThan200() {
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят " +
                "разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                "который за время «своего отсутствия», стал кандидатом Коломбани.");
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(film)
        );
        assertEquals("Максимальная длина описания — 200 символов.", exp.getMessage(), "Валидация " +
                "по описанию не прошла");
        assertEquals(0, filmService.getAllFilms().size());
    }

    @Test
    void shouldAddFilmWhenDescriptionIs200() throws ValidationException, DataExistException {
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят " +
                "разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                "который за время «сво");
        filmService.addFilm(film);
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals(filmService.getAllFilms().get(0).getDescription().length(), 200, "Размер описания" +
                "после обновления не корректный");
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIs201() {
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят " +
                "разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                "который за время «сво ");
        assertEquals(film.getDescription().length(), 201, "Размер описания не корректный");
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(film)
        );
        assertEquals("Максимальная длина описания — 200 символов.", exp.getMessage(), "Валидация " +
                "по описанию не прошла");
        assertEquals(0, filmService.getAllFilms().size());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndDescriptionIsMoreThan200() throws ValidationException, DataExistException {
        addNewFilm();
        assertEquals(1, filmService.getAllFilms().size());
        Film filmUpdated = Film.builder()
                .id(film.getId())
                .name("FilName")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят " +
                        "разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                        "который за время «своего отсутствия», стал кандидатом Коломбани.")
                .releaseDate(LocalDate.of(2000, 12,12))
                .duration(90)
                .build();
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> filmService.updateFilm(filmUpdated)
        );
        assertEquals("Максимальная длина описания — 200 символов.", exp.getMessage(), "Валидация " +
                "по описанию не прошла");
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals("Description1", filmService.getAllFilms().get(0).getDescription(), "Описание " +
                "после обновления не корректное");
    }

    @Test
    void shouldUpdateFilmWhenDescriptionIs200() throws ValidationException, DataExistException {
        addNewFilm();
        Film filmUpdated = Film.builder()
                .id(film.getId())
                .name("FilName")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят " +
                        "разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. " +
                        "о Куглов, который за время «сво")
                .releaseDate(LocalDate.of(2000, 12,12))
                .duration(90)
                .mpa(Mpa.builder().id(1).build())
                .build();

        filmService.updateFilm(filmUpdated);
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals(filmService.getAllFilms().get(0).getDescription().length(), 200, "Размер описания" +
                "после обновления не корректный");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndDescriptionIs201() throws ValidationException, DataExistException {
        addNewFilm();
        assertEquals(1, filmService.getAllFilms().size());
        Film filmUpdated = Film.builder()
                .id(film.getId())
                .name("FilName")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят " +
                        "разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. " +
                        "о Куглов, который за время «свой")
                .releaseDate(LocalDate.of(2000, 12,12))
                .duration(90)
                .build();
        assertEquals(filmUpdated.getDescription().length(), 201, "Размер описания не корректный");

        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> filmService.updateFilm(filmUpdated)
        );
        assertEquals("Максимальная длина описания — 200 символов.", exp.getMessage(), "Валидация " +
                "по описанию не прошла");
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals("Description1", filmService.getAllFilms().get(0).getDescription(), "Описание " +
                "после обновления не корректное");
    }

    @Test
    void shouldAddFilmWithReleaseDate28121895() throws ValidationException, DataExistException {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        filmService.addFilm(film);
        assertEquals(1, filmService.getAllFilms().size());
    }

    @Test
    void shouldUpdateFilmWithReleaseDate28121895() throws ValidationException, DataExistException {
        addNewFilm();
        assertEquals(1, filmService.getAllFilms().size());
        Film filmUpdated = Film.builder()
                .id(film.getId())
                .name("FilName")
                .description("Description1")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(90)
                .mpa(Mpa.builder().id(1).build())
                .build();

        filmService.updateFilm(filmUpdated);
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals(LocalDate.of(1895, 12, 28),
                filmService.getAllFilms().get(0).getReleaseDate(), "Дата релиза после обновления" +
                        "не корректная.");
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsBefore28121895() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(film)
        );
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exp.getMessage(),
                "Валидация по дате релиза не прошла");
        assertEquals(0, filmService.getAllFilms().size());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndReleaseDateIsBefore28121895() throws ValidationException, DataExistException {
        addNewFilm();
        assertEquals(1, filmService.getAllFilms().size());
        Film filmUpdated = Film.builder()
                .id(film.getId())
                .name("FilName")
                .description("Description1")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(90)
                .mpa(Mpa.builder().id(1).build())
                .build();
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> filmService.updateFilm(filmUpdated)
        );
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exp.getMessage(),
                "Валидация по дате релиза не прошла");
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals(LocalDate.of(2000, 12,12),
                filmStorage.getAllFilms().get(0).getReleaseDate(), "Дата релиза после обновления не корректная");
    }

    @Test
    void shouldAddFilmWhenDurationIs1() throws ValidationException, DataExistException {
        film.setDuration(1);
        filmStorage.addFilm(film);
        assertEquals(1, filmStorage.getAllFilms().size());
    }

    @Test
    void shouldUpdateFilmWhenDurationIs1() throws ValidationException, DataExistException {
        addNewFilm();
        assertEquals(1, filmService.getAllFilms().size());
        Film filmUpdated = Film.builder()
                .id(film.getId())
                .name("FilName")
                .description("Description1")
                .releaseDate(LocalDate.of(1895, 12, 29))
                .duration(1)
                .mpa(Mpa.builder().id(1).build())
                .build();

        filmService.updateFilm(filmUpdated);
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals(1, filmService.getAllFilms().get(0).getDuration(), "Продолжительность после " +
                "обновления не корректная.");
    }

    @Test
    void shouldThrowExceptionWhenDurationIs0() {
        film.setDuration(0);
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(film)
        );
        assertEquals("Продолжительность фильма должна быть положительной.", exp.getMessage(),
                "Валидация по продолжительности не прошла");
        assertEquals(0, filmService.getAllFilms().size());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndDurationIs0() throws ValidationException, DataExistException {
        addNewFilm();
        assertEquals(1, filmService.getAllFilms().size());
        Film filmUpdated = Film.builder()
                .id(film.getId())
                .name("FilName")
                .description("Description1")
                .releaseDate(LocalDate.of(1895, 12, 29))
                .duration(0)
                .mpa(Mpa.builder().id(1).build())
                .build();
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> filmService.updateFilm(filmUpdated)
        );
        assertEquals("Продолжительность фильма должна быть положительной.", exp.getMessage(),
                "Валидация по продолжительности не прошла");
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals(90, filmService.getAllFilms().get(0).getDuration(),
                "Продолжительность после обновления не корректная");
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        film.setDuration(-1);
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(film)
        );
        assertEquals("Продолжительность фильма должна быть положительной.", exp.getMessage(),
                "Валидация по продолжительности не прошла");
        assertEquals(0, filmService.getAllFilms().size());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndDurationIsNegative() throws ValidationException, DataExistException {
        addNewFilm();
        assertEquals(1, filmService.getAllFilms().size());
        Film filmUpdated = Film.builder()
                .id(film.getId())
                .name("FilName")
                .description("Description1")
                .releaseDate(LocalDate.of(1895, 12, 29))
                .duration(-1)
                .mpa(Mpa.builder().id(1).build())
                .build();
        ValidationException exp = assertThrows(
                ValidationException.class,
                () -> filmService.updateFilm(filmUpdated)
        );
        assertEquals("Продолжительность фильма должна быть положительной.", exp.getMessage(),
                "Валидация по продолжительности не прошла");
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals(90, filmService.getAllFilms().get(0).getDuration(),
                "Продолжительность после обновления не корректная");
    }

    @Test
    void shouldThrowExceptionWhenEmptyDescription() {
        film.setDescription("");
        assertEquals(0, filmService.getAllFilms().size(), "Валидация пропустила пустую строку");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndEmptyDescription() throws ValidationException, DataExistException {
        addNewFilm();
        assertEquals(1, filmService.getAllFilms().size());
        Film filmUpdated = Film.builder()
                .id(film.getId())
                .name("FilmName")
                .description("")
                .releaseDate(LocalDate.of(2000, 12,12))
                .duration(90)
                .mpa(Mpa.builder().id(1).build())
                .build();
        filmService.updateFilm(filmUpdated);
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals(film.getDescription(), "Description1", "Название фильма после обновления неверное");
    }

    @Test
    void shouldThrowExceptionWhenBlankDescription() {
        film.setDescription(" ");
        assertEquals(0, filmService.getAllFilms().size(), "Валидация пропустила пустую строку");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndBlankDescription() throws ValidationException, DataExistException {
        addNewFilm();
        assertEquals(1, filmService.getAllFilms().size());
        Film filmUpdated = Film.builder()
                .id(film.getId())
                .name("FilmName")
                .description(" ")
                .releaseDate(LocalDate.of(2000, 12,12))
                .duration(90)
                .mpa(Mpa.builder().id(1).build())
                .build();
        filmService.updateFilm(filmUpdated);
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals(film.getDescription(), "Description1", "Название фильма после обновления неверное");
    }

    @Test
    void shouldThrowExceptionWhenNullRelease() {
        film.setReleaseDate(null);
        assertEquals(0, filmService.getAllFilms().size(), "Валидация пропустила пустую строку");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndNullRelease() throws DataExistException {
        addNewFilm();
        assertEquals(1, filmService.getAllFilms().size());
        Film filmUpdated = Film.builder()
                .id(film.getId())
                .name("FilmName")
                .description("Descr")
                .releaseDate(null)
                .duration(90)
                .mpa(Mpa.builder().id(1).build())
                .build();
        filmService.updateFilm(filmUpdated);
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals(film.getReleaseDate(), LocalDate.of(2000, 12,12), "Название фильма после обновления неверное");
    }

    @Test
    void findFilmById() throws DataExistException {
        addNewFilm();
        Film film2 = Film.builder()
                .id(0)
                .name("FilName")
                .description("Description1")
                .releaseDate(LocalDate.of(2000, 12,12))
                .duration(90)
                .mpa(Mpa.builder().id(1).build())
                .build();
        filmService.addFilm(film2);
        assertEquals(2, filmService.getAllFilms().size(), "Количество фильмов неверное.");
        assertEquals(1, filmService.getFilmById(1).getId(), "Возвращается не верный фильм.");
    }

}