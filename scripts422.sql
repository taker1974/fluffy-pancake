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
CREATE TABLE vehicle (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(50) NOT NULL CHECK (LENGTH(brand) BETWEEN 3 AND 50),
    model VARCHAR(50) NOT NULL CHECK (LENGTH(model) BETWEEN 1 AND 50),
    price NUMERIC(12, 2) NOT NULL CHECK (price >= 0),
	UNIQUE (brand, model)
);

-- Водитель
CREATE TABLE driver (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE CHECK (LENGTH(name) BETWEEN 1 AND 50),
    age INT NOT NULL CHECK (age >= 18),
    have_license BOOLEAN NOT NULL
);

-- ТС и их автомобили (многие-ко-многим)
CREATE TABLE vehicle_driver (
    vehicle_id INT NOT NULL,
    driver_id INT NOT NULL,
    PRIMARY KEY (vehicle_id, driver_id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id),
    FOREIGN KEY (driver_id) REFERENCES driver(id)
);

INSERT INTO vehicle (brand, model, price) VALUES ('BrandA', 'Model A', '50000');
INSERT INTO vehicle (brand, model, price) VALUES ('BrandA', 'Model B', '30000');
INSERT INTO vehicle (brand, model, price) VALUES ('BrandA', 'Model Y', '80000');

INSERT INTO vehicle (brand, model, price) VALUES ('BrandB', 'Model A', '123000');
INSERT INTO vehicle (brand, model, price) VALUES ('BrandB', 'model Z350', '370000');

INSERT INTO vehicle (brand, model, price) VALUES ('BrandF', 'Model 150', '45000');
INSERT INTO vehicle (brand, model, price) VALUES ('BrandF', 'model 250', '60000');

INSERT INTO driver (name, age, have_license) VALUES ('Иван', '18', 'false');
INSERT INTO driver (name, age, have_license) VALUES ('Потап', '19', 'true');
INSERT INTO driver (name, age, have_license) VALUES ('Прокл', '20', 'true');
INSERT INTO driver (name, age, have_license) VALUES ('Свистоперд', '38', 'true');
INSERT INTO driver (name, age, have_license) VALUES ('Святожуй', '35', 'true');
INSERT INTO driver (name, age, have_license) VALUES ('Амвросий', '48', 'true');
INSERT INTO driver (name, age, have_license) VALUES ('Амвруаз', '88', 'false');

INSERT INTO vehicle_driver (vehicle_id, driver_id) VALUES ('1', '2');
INSERT INTO vehicle_driver (vehicle_id, driver_id) VALUES ('1', '4');
INSERT INTO vehicle_driver (vehicle_id, driver_id) VALUES ('1', '7');

INSERT INTO vehicle_driver (vehicle_id, driver_id) VALUES ('5', '2');
INSERT INTO vehicle_driver (vehicle_id, driver_id) VALUES ('5', '4');
INSERT INTO vehicle_driver (vehicle_id, driver_id) VALUES ('7', '1');

SELECT
    vd.vehicle_id,
    v.brand,
    v.model,
    v.price,
    vd.driver_id,
    d.name,
    d.age,
    d.have_license
FROM
    vehicle_driver vd
INNER JOIN
    vehicle v on vd.vehicle_id = v.id
INNER JOIN
    driver d on vd.driver_id = d.id;
