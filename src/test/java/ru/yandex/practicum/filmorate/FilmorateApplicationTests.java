package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.DataExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmDbStorage;
	private final JdbcTemplate jdbcTemplate;

	@AfterEach
	void clearDB() {
		jdbcTemplate.update("DELETE FROM FILM_USER");
		jdbcTemplate.update("DELETE FROM FILM_GENRE");
		jdbcTemplate.update("DELETE FROM FRIEND_TABLE");
		jdbcTemplate.update("DELETE FROM USER_TABLE");
		jdbcTemplate.update("DELETE FROM FILM_TABLE");
		jdbcTemplate.update("ALTER TABLE USER_TABLE ALTER COLUMN USER_ID RESTART WITH 1");
		jdbcTemplate.update("ALTER TABLE FILM_TABLE ALTER COLUMN FILM_ID RESTART WITH 1");
	}

	private User createUser() {
		return User.builder()
				.id(0)
				.email("email@mail.ru")
				.login("Login")
				.name("Name")
				.birthday(LocalDate.of(2000, 2, 20))
				.build();
	}

	private Film createFilm() {
		return Film.builder()
				.id(0)
				.name("FilName")
				.description("Description1")
				.releaseDate(LocalDate.of(2000, 12,12))
				.duration(90)
				.mpa(Mpa.builder().id(1).build())
				.build();
	}

	@Test
	public void addUser() throws DataExistException, ValidationException {
		assertEquals(0, userStorage.getAllUsers().size(), "Количество пользователей неверное");
		userStorage.addUser(createUser());
		assertEquals(1, userStorage.getAllUsers().size(), "Количество пользователей неверное");
		assertEquals(1,userStorage.getUserById(1).getId(), "Пользователь возвращается некорректно.");
	}

	@Test
	public void updateUser() throws DataExistException, ValidationException {
		assertEquals(0, userStorage.getAllUsers().size(), "Количество пользователей неверное");
		addUser();
		User userUpdated = User.builder()
				.id(1)
				.email("emailUpdated@mail.ru")
				.login("Login")
				.name("Name")
				.birthday(LocalDate.of(2000, 2, 20))
				.build();
		userStorage.updateUser(userUpdated);
		assertEquals(1, userStorage.getAllUsers().size(), "Количество пользователей неверное");
		assertEquals("emailUpdated@mail.ru", userStorage.getAllUsers().get(0).getEmail(),
				"Неверная почта после обновления");

	}
	@Test
	public void getAllUsers() throws DataExistException {
		addUser();
		userStorage.addUser(User.builder()
				.id(0)
				.email("email@mail.ru")
				.login("Login2")
				.name("Name2")
				.birthday(LocalDate.of(1996, 2, 20))
				.build());
		assertEquals(2, userStorage.getAllUsers().size(), "Количество пользователей неверное");
		assertEquals(2,userStorage.getUserById(2).getId(), "Пользователь возвращается некорректно.");
	}

	@Test
	public void getUserById() throws DataExistException{
		addUser();
		assertEquals(1,userStorage.getUserById(1).getId(), "Пользователь возвращается некорректно.");
	}
	@Test
	public void removeUserById() throws DataExistException{
		addUser();
		assertEquals(1,userStorage.getAllUsers().size(), "Некорректное количество пользователей.");
		userStorage.removeUserById(1);
		assertEquals(0,userStorage.getAllUsers().size(), "Некорректное количество пользователей.");

	}
	@Test
	public void addFriend() throws DataExistException{
		addUser();
		userStorage.addUser(User.builder()
				.id(0)
				.email("friendemail@mail.ru")
				.login("Friend")
				.name("FriendName")
				.birthday(LocalDate.of(1996, 2, 22))
				.build());
		assertEquals(2,userStorage.getAllUsers().size(), "Некорректное количество пользователей.");
		userStorage.addFriend(1, 2);
		assertEquals(1, userStorage.getAllFriends(1).size(), "Количество друзей неверное.");
		assertEquals(0, userStorage.getAllFriends(2).size(), "Количество друзей неверное.");
		assertEquals(2, userStorage.getAllFriends(1).get(0).getId(), "Друг возвращается некорректно.");
	}

	@Test
	public void getCommonFriends() throws DataExistException{
		addFriend();
		userStorage.addUser(User.builder()
				.id(0)
				.email("commonFriend@mail.ru")
				.login("Common")
				.name("CommonFriendName")
				.birthday(LocalDate.of(1996, 2, 21))
				.build());
		assertEquals(3,userStorage.getAllUsers().size(), "Некорректное количество пользователей.");
		userStorage.addFriend(1, 3);
		userStorage.addFriend(2, 3);
		assertEquals(1, userStorage.getCommonFriends(1, 2).size(),
				"Общий друг возвращается некорректно");
		assertEquals(3, userStorage.getCommonFriends(1, 2).get(0).getId(),
				"Общий друг возвращается некорректно");
	}
	@Test
	public void removeFriend() throws DataExistException{
		addFriend();
		assertEquals(1, userStorage.getAllFriends(1).size(), "Количество друзей неверное.");
		userStorage.removeFriend(1, 2);
		assertEquals(0, userStorage.getAllFriends(1).size(), "Количество друзей неверное.");

	}

	@Test
	public void addFilm() throws ValidationException, DataExistException {
		filmDbStorage.addFilm(createFilm());
		assertEquals(1, filmDbStorage.getAllFilms().size(), "Количество фильмо возвращается некорректное.");
		assertEquals(1, filmDbStorage.getFilmById(1).getId(), "Фильм по id возвращается некорректно");
	}

	@Test
	public void updateFilm() throws DataExistException, ValidationException {
		addFilm();
		filmDbStorage.updateFilm(Film.builder()
				.id(1)
				.name("Updated Name")
				.description("Description1")
				.releaseDate(LocalDate.of(2000, 12,12))
				.duration(90)
				.mpa(Mpa.builder().id(3).build())
				.build());
		assertEquals(1, filmDbStorage.getAllFilms().size(), "Количество фильмов возвращается некорректное.");
		assertEquals("Updated Name", filmDbStorage.getFilmById(1).getName(),
				"Имя фильма после обновления не изменилось");
	}

	@Test
	public void removeFilmById() throws DataExistException {
		addFilm();
		assertEquals(1, filmDbStorage.getAllFilms().size(), "Количество фильмов возвращается некорректное.");
		filmDbStorage.removeFilmById(1);
		assertEquals(0, filmDbStorage.getAllFilms().size(), "Количество фильмов возвращается некорректное.");
	}

	@Test
	public void getAllFilms() throws DataExistException {
		addFilm();
		filmDbStorage.addFilm(Film.builder()
				.id(0)
				.name("FilmName")
				.description("Description2")
				.releaseDate(LocalDate.of(2011, 11,12))
				.duration(120)
				.mpa(Mpa.builder().id(2).build())
				.build());
		assertEquals(2, filmDbStorage.getAllFilms().size(), "Количество фильмов возвращается некорректное.");
	}

	@Test
	public void getFilmById() throws DataExistException {
		addFilm();
		assertEquals(1, filmDbStorage.getFilmById(1).getId(), "Фильм по id возвращается некорректно");
	}

	@Test
	public void addAndRemoveLike() throws DataExistException {
		addFilm();
		addUser();
		assertEquals(0, filmDbStorage.getFilmById(1).getLikes().size(), "Колличество лайков не корректное");
		filmDbStorage.addLike(1L, 1L);
		assertEquals(1, filmDbStorage.getFilmById(1).getLikes().size(), "Колличество лайков не корректное");
		filmDbStorage.removeLike(1L, 1L);
		assertEquals(0, filmDbStorage.getFilmById(1).getLikes().size(), "Колличество лайков не корректное");
	}
}
