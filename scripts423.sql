--Шаг 3
--Составить первый JOIN-запрос, чтобы получить информацию обо всех студентах
--(достаточно получить только имя и возраст студента) школы Хогвартс вместе с названиями факультетов.
--Составить второй JOIN-запрос, чтобы получить только тех студентов, у которых есть аватарки.
--В корне проекта создать файл scripts423.sql и поместить в него запрос.

-- Всё тестировалось в PgAdmin 4.
-- Плагин DB Browser для IDEA Community на таких запросах начинает ошибаться.

insert into faculty (name, color) values ('Faculty 01', 'Red');
insert into faculty (name, color) values ('Faculty 02', 'Green');
insert into faculty (name, color) values ('Faculty 03', 'Blue');

insert into student(name) values('name 1');
insert into student(name) values('name 3');
insert into student(name) values('name 4');
insert into student(name) values('name 5');
insert into student(name) values('name 6');

update student set faculty_id='1' where name='name 1';
update student set faculty_id='1' where name='name 4';
update student set faculty_id='3' where name='name 3';

select * from faculty;
select * from student;

-- Первый JOIN-запрос
SELECT
    s.id,
    s.name,
    s.age,
    s.avatar_id,
    f.name
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
    f.name
FROM
    student s
INNER JOIN
    faculty f ON s.faculty_id = f.id
WHERE
    s.avatar_id IS NOT NULL;
