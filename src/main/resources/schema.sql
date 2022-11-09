CREATE TABLE if not exists mpa (
                                   mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                   name VARCHAR
);

create table if not exists film_table (
                                          film_id LONG GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                          name VARCHAR NOT NULL,
                                          description VARCHAR(200),
                                          release_date DATE,
                                          film_duration INTEGER,
                                          mpa_id INTEGER REFERENCES mpa(mpa_id)
);

CREATE TABLE if not exists genre (
                                     genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                     name VARCHAR
);

CREATE TABLE if not exists film_genre (
                                          film_id LONG REFERENCES film_table(film_id),
                                          genre_id INTEGER REFERENCES genre(genre_id)
);

CREATE TABLE if not exists user_table (
                                          user_id LONG GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                          name VARCHAR,
                                          login VARCHAR NOT NULL CHECK (login not like ' '),
                                          birthday DATE,
                                          email VARCHAR NOT NULL
);

CREATE TABLE if not exists friend_table (
                                            user_id LONG REFERENCES user_table(user_id),
                                            friend_id LONG REFERENCES user_table(user_id)
);

CREATE TABLE if not exists film_user (
                                         film_id LONG REFERENCES film_table(film_id),
                                         user_id LONG REFERENCES user_table(user_id)
);