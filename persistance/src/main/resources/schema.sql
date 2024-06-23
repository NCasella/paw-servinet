--DROP TYPE IF EXISTS serviceCategory;
--DROP TYPE IF EXISTS pricingType;

CREATE TABLE IF NOT EXISTS users (
    userid SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telephone VARCHAR(255),
    isprovider boolean NOT NULL DEFAULT FALSE
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS locale varchar(10) DEFAULT 'en' NOT NULL;


CREATE TABLE IF NOT EXISTS business(
  businessid SERIAL PRIMARY KEY,
  userid INT references users(userid) ON DELETE CASCADE,
  businessname VARCHAR(255),
  businessTelephone VARCHAR(255),
  businessEmail VARCHAR(255),
  businessLocation VARCHAR(255)
);
CREATE TABLE IF NOT EXISTS images(
  imageid SERIAL PRIMARY KEY,
  imageBytes BYTEA
);

--CREATE TYPE serviceCategory AS ENUM ('Limpieza', 'Belleza', 'Arreglos calificados', 'Mascotas', 'Exteriores', 'Eventos y Celebraciones', 'Transporte', 'Consultoria', 'Salud');
--CREATE TYPE pricingType AS ENUM ('Per hour', 'Per total', 'Budget', 'TBD');

CREATE TABLE IF NOT EXISTS services (
    id SERIAL PRIMARY KEY,
    businessid INT REFERENCES business(businessid) ON DELETE CASCADE,
    servicename VARCHAR(255) NOT NULL,
    servicedescription VARCHAR(255),
    homeservice BOOLEAN,
    location VARCHAR(255) NOT NULL,
    category  VARCHAR(50) CHECK (category IN ('Limpieza', 'Belleza', 'Arreglos Calificados','Peluqueria', 'Mascotas', 'Exteriores', 'Eventos y Celebraciones', 'Transporte', 'Consultoria', 'Salud')),
    minimalduration INT,
    pricingtype  VARCHAR(50) CHECK (pricingtype IN ('Por hora', 'Total', 'Producto', 'A determinar')),
    price VARCHAR(255),
    additionalcharges BOOLEAN,
    imageId INT references images(imageid),
    neighbourhood VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS nbservices(insertid serial primary key ,
    serviceid int references services(id) on delete cascade ,
    neighbourhood varchar(60)
);

--script para generar la nueva tabla
--begin transaction;
--alter table services add neighbourhood varchar(255);
--update services set neighbourhood = split_part(location,';',1);
--update services set location = split_part(location,';',2);
--create table nbservices(insertid serial primary key, serviceid int references services(id) on delete cascade, neighbourhood text);
--insert into nbservices(serviceid,neighbourhood) select id,neighbourhood from services;
--commit;

CREATE TABLE IF NOT EXISTS appointments (
    appointmentid SERIAL PRIMARY KEY,
    serviceid INT REFERENCES services ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    userid INT REFERENCES users ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    startDate TIMESTAMP NOT NULL,
    endDate TIMESTAMP,
    location VARCHAR(255),
    confirmed BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS questions (
    questionid SERIAL PRIMARY KEY,
    serviceid INT REFERENCES services ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    userid INT REFERENCES users ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    question VARCHAR(255) NOT NULL,
    response VARCHAR(255),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ratings (
     ratingid SERIAL PRIMARY KEY,
     serviceid INT REFERENCES services ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
     userid INT REFERENCES users ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
     rating INTEGER CHECK (rating >= 1 AND rating <= 5) NOT NULL,
     comment VARCHAR(255),
     date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS passwordrecoverycodes (
    userid INT REFERENCES users ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
--    code VARCHAR(255) UNIQUE NOT NULL,
    code uuid UNIQUE NOT NULL,
    expirationdate TIMESTAMP
);


--insert into users  (username, password, name, surname, email, telephone,isprovider)  values ('admin', 'admin', 'adminname', 'adminlastname','admin@mail.com','123456789', true);
--insert into business (userid, businessname, businessTelephone, businessEmail, businessLocation) values (1, 'adminbusiness', '123456789','adminbusiness@mail.com','Almagro');



-- HARDCODIE INSERTS, BORRARRRRRRR {
--INSERT INTO users (userid, username, password, name, surname, email, telephone) VALUES (1000, 'username', 'password', 'name', 'surname', 'email', 'telephone');
--INSERT INTO business VALUES (1, 1000, 'businessname', 'businessTelephone', 'peluqueria@gmail.com', 'businessLocation');
--INSERT INTO services VALUES (1,1,'Peluqueria Ramon','Veni, peinate y divertite!',false,'Recoleta','Belleza',60,'Per Hour',5000,true);
-- } NO OLVIDAR DE BORRAR

