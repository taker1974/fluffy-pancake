--Шаг 2
--В этом задании по описанию необходимо спроектировать таблицы, связи между ними и корректно определить типы данных.
--Здесь не важно, какой тип вы выберете, например, для данных, представленных в виде строки (varchar или text).
--Важно, что вы выберете один из строковых типов, а не числовых, например.
--Описание структуры: у каждого человека есть машина. Причем несколько человек могут пользоваться одной машиной.
--У каждого человека есть имя, возраст и признак того, что у него есть права (или их нет).
--У каждой машины есть марка, модель и стоимость.
--Также не забудьте добавить таблицам первичные ключи и связать их.
--В корне проекта нужно создать файл scripts422.sql и поместить в него запросы для создания таблиц.

-- Всё тестировалось в PgAdmin 4.
-- Плагин DB Browser для IDEA Community на таких запросах начинает ошибаться.

-- ТС
create TABLE Vehicle (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(50) NOT NULL CHECK (LENGTH(brand) BETWEEN 3 AND 50),
    model VARCHAR(50) NOT NULL CHECK (LENGTH(model) BETWEEN 1 AND 50),
    price NUMERIC(12, 2) NOT NULL CHECK (price >= 0),
	UNIQUE (brand, model)
);

-- Водитель
create TABLE Driver (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE CHECK (LENGTH(name) BETWEEN 1 AND 50),
    age INT NOT NULL CHECK (age >= 18),
    have_license BOOLEAN NOT NULL
);

-- CREATE TABLE Rent (
--     id SERIAL PRIMARY KEY,
--     vehicle_id INT NOT NULL REFERENCES Vehicle(id),
--     driver_id INT NOT NULL REFERENCES Driver(id),
--     UNIQUE (vehicle_id, driver_id)
-- );

-- ТС и их автомобили (многие-ко-многим)
create TABLE Vehicle_Driver (
    vehicle_id INT NOT NULL,
    driver_id INT NOT NULL,
    PRIMARY KEY (vehicle_id, driver_id),  -- Составной первичный ключ
    FOREIGN KEY (vehicle_id) REFERENCES Vehicle(id),
    FOREIGN KEY (driver_id) REFERENCES Driver(id)
);

insert into Vehicle (brand, model, price) values ('BrandA', 'Model A', '50000');
insert into Vehicle (brand, model, price) values ('BrandA', 'Model B', '30000');
insert into Vehicle (brand, model, price) values ('BrandA', 'Model Y', '80000');

insert into Vehicle (brand, model, price) values ('BrandB', 'Model A', '123000');
insert into Vehicle (brand, model, price) values ('BrandB', 'model Z350', '370000');

insert into Vehicle (brand, model, price) values ('BrandF', 'Model 150', '45000');
insert into Vehicle (brand, model, price) values ('BrandF', 'model 250', '60000');

-- ошибка вставки
--insert into Vehicle (brand, model, price) values ('BrandF', 'model 250', '60000');

insert into Driver (name, age, have_license) values ('Иван', '18', 'false');
insert into Driver (name, age, have_license) values ('Потап', '19', 'true');
insert into Driver (name, age, have_license) values ('Прокл', '20', 'true');
insert into Driver (name, age, have_license) values ('Свистоперд', '38', 'true');
insert into Driver (name, age, have_license) values ('Святожуй', '35', 'true');
insert into Driver (name, age, have_license) values ('Амвросий', '48', 'true');
insert into Driver (name, age, have_license) values ('Амвруаз', '88', 'false');

-- ошибка вставки
--insert into Driver (name, age, have_license) values ('Амвросий', '49', 'true');

select * from Vehicle;
select * from Driver;

insert into Vehicle_Driver (vehicle_id, driver_id) values ('1', '2');
insert into Vehicle_Driver (vehicle_id, driver_id) values ('1', '4');
insert into Vehicle_Driver (vehicle_id, driver_id) values ('1', '7');

insert into Vehicle_Driver (vehicle_id, driver_id) values ('5', '2');
insert into Vehicle_Driver (vehicle_id, driver_id) values ('5', '4');
insert into Vehicle_Driver (vehicle_id, driver_id) values ('7', '1');

-- ошибка вставки
--insert into Vehicle_Driver (vehicle_id, driver_id) values ('1', '666');

--select * from Vehicle_Driver;
select
    vd.vehicle_id,
    v.brand,
    v.model,
    v.price,
    vd.driver_id,
    d.name,
    d.age,
    d.have_license
from
    Vehicle_Driver vd
inner join
    Vehicle v on vd.vehicle_id = v.id
inner join
    Driver d on vd.driver_id = d.id;
-- WHERE
-- 	d.have_license = 'true';
