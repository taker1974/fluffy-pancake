-- liquibase formatted sql

--Шаг 1
--Добавить Liquibase к проекту.
--Создать файл для миграций с любым говорящим названием, которое относится к текущему уроку.
--В качестве названия можно использовать, например: lesson-three, course-four-lesson-three, index-practice и т.д.
--Кажется, имя student.sql нормально соотносится с темой урока :)

--Шаг 2
--Добавить два индекса, используя миграции:
--Индекс для поиска по имени студента.
--Индекс для поиска по названию и цвету факультета (см. faculty.sql).

-- changeset kostusonline:1
CREATE INDEX student_name_index ON student (name);
CREATE INDEX users_sh_idx ON users (street, house);