-- liquibase formatted sql

--Шаг 2
--Добавить два индекса, используя миграции:
--Индекс для поиска по имени студента.
--Индекс для поиска по названию и цвету факультета (см. faculty.sql).

-- changeset kostusonline:1
CREATE INDEX faculty_name_color_index ON faculty (name, color);