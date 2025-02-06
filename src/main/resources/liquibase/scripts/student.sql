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

-- changeSet kostusonline:2 runOnChange:true
CREATE INDEX IF NOT EXISTS student_name_index ON student (name);
