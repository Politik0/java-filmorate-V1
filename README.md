# Filmorate data base map
![This is an image](https://github.com/Politik0/java-filmorate/blob/main/filmorateDB.png)

## Примеры запросов
### Получение ТОП-10 фильмов
```sql
SELECT f.title
FROM film AS f
RIGHT JOIN (
        SELECT film_id,
               COUNT(user_id)
        FROM like
        GROUP BY film_id
        ORDER BY COUNT(user_id) DESC
        LIMIT 10) AS fr ON f.film_id=fr.film_id
```

### Получение списка пользователей, которые поставили лайк фильму
```sql
SELECT u.login
FROM like AS l 
LEFT JOIN user AS u ON l.user_id=u.user_id
WHERE l.film_id=3
```

### Запрос списка общих друзей
```sql
SELECT other_user_id
FROM (
    SELECT COUNT(user_id),
        other_user_id
    FROM friend
    WHERE user_id=1
    AND user_id=2
    AND friend_status=1 --подтвержденный
GROUP BY other_user_id)
HAVING COUNT(user_id) > 1
```

### Получение фильмов без возрастных ограничений
```sql
SELECT*
FROM film
WHERE rating_id=1 --G — у фильма нет возрастных ограничений
```
