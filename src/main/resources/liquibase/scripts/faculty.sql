-- liquibase formatted sql

--Шаг 2
--Добавить два индекса, используя миграции:
--Индекс для поиска по имени студента.
--Индекс для поиска по названию и цвету факультета (см. faculty.sql).

-- changeSet kostusonline:2 runOnChange:true
CREATE INDEX IF NOT EXISTS faculty_name_color_index ON faculty (name, color);