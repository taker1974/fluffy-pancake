-- liquibase formatted sql

-- changeset kostusonline:1
delete from databasechangelog;
delete from databasechangeloglock;
delete from logs;

drop table databasechangelog;
drop table databasechangeloglock;
drop table logs;

drop index faculty_name_color_index;
drop index student_name_index;
