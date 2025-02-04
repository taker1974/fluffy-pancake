--Шаг 1
--С прошлых уроков у нас есть две таблицы: Student и Faculty. Необходимо для них создать следующие ограничения:
--    - Возраст студента не может быть меньше 16 лет.
--    - Имена студентов должны быть уникальными и не равны нулю.
--    - Пара “значение названия” - “цвет факультета” должна быть уникальной.
--    - При создании студента без возраста ему автоматически должно присваиваться 20 лет.
--В корне проекта нужно создать файл scripts421.sql (что значит 4-й курс, 2-й урок, 1-е задание)
--и поместить в него запросы для создания ограничений.

-- Всё тестировалось в PgAdmin 4.
-- Плагин DB Browser для IDEA Community на таких запросах начинает ошибаться.

ALTER TABLE student ADD CONSTRAINT age_min_constraint CHECK (age >= 16);
ALTER TABLE student ALTER COLUMN age SET DEFAULT 20;

ALTER TABLE student ALTER COLUMN name SET NOT NULL;
ALTER TABLE student ADD CONSTRAINT name_unique_constraint UNIQUE (name);

ALTER TABLE faculty ADD CONSTRAINT name_color_unique_constraint UNIQUE (name, color);
