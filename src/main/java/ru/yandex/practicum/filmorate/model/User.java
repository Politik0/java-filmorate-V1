package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.ValidationException;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Data
@Builder
public class User {
    private long id;

    private String name;
    @NotNull
    @NotBlank
    private String login;
    @NotNull
    //@PastOrPresent(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;
    @Email
    private String email;
    @Builder.Default
    private Set<Long> friends = new TreeSet<>();

/*    public User(long id, String name, String login, LocalDate birthday, String email) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.birthday = birthday;
        this.email = email;
    }*/

    public void addFriend(long id) {
        friends.add(id);
    }

    public void removeFriend(long id) {
        friends.remove(id);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("login", login);
        values.put("birthday", birthday);
        values.put("email", email);
        return values;
    }
}
