--Шаг 3
--Составить первый JOIN-запрос, чтобы получить информацию обо всех студентах
--(достаточно получить только имя и возраст студента) школы Хогвартс вместе с названиями факультетов.
--Составить второй JOIN-запрос, чтобы получить только тех студентов, у которых есть аватарки.
--В корне проекта создать файл scripts423.sql и поместить в него запрос.

-- Всё тестировалось в PgAdmin 4.
-- Плагин DB Browser для IDEA Community на таких запросах начинает ошибаться.

INSERT INTO faculty (name, color) VALUES ('Faculty 01', 'Red');
INSERT INTO faculty (name, color) VALUES ('Faculty 02', 'Green');
INSERT INTO faculty (name, color) VALUES ('Faculty 03', 'Blue');

INSERT INTO student(name) VALUES('name 1');
INSERT INTO student(name) VALUES('name 3');
INSERT INTO student(name) VALUES('name 4');
INSERT INTO student(name) VALUES('name 5');
INSERT INTO student(name) VALUES('name 6');

UPDATE student SET faculty_id='1' WHERE name='name 1';
UPDATE student SET faculty_id='1' WHERE name='name 4';
UPDATE student SET faculty_id='3' WHERE name='name 3';

SELECT * FROM faculty;
SELECT * FROM student;

-- Первый JOIN-запрос
SELECT
    s.id,
    s.name,
    s.age,
    s.avatar_id,
    f.name AS faculty_name
FROM
    student s
INNER JOIN
    faculty f ON s.faculty_id = f.id;

-- Второй JOIN-запрос
SELECT
    s.id,
    s.name,
    s.age,
	s.avatar_id,
    f.name AS faculty_name
FROM
    student s
INNER JOIN
    faculty f ON s.faculty_id = f.id
WHERE
    s.avatar_id IS NOT NULL;
