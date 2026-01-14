CREATE TABLE IF NOT EXISTS users (
    userid SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, --todo: sacar el default null una vez que se implemente el login
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telephone VARCHAR(255),
    isprovider boolean NOT NULL DEFAULT FALSE,
    Locale varchar(20),
    isverified boolean not null,
    profile_pic int null
);

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
    imageBytes binary
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
    imageId INT references images(imageid)
);

CREATE TABLE IF NOT EXISTS nbservices(
    insertid serial primary key ,
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
    confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    cancelled BOOLEAN NOT NULL DEFAULT FALSE,
    description VARCHAR(255)
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

-- Sequence for users table
CREATE SEQUENCE IF NOT EXISTS users_userid_seq;

-- Sequence for business table
CREATE SEQUENCE IF NOT EXISTS business_businessid_seq;

-- Sequence for images table
CREATE SEQUENCE IF NOT EXISTS images_imageid_seq;

-- Sequence for services table
CREATE SEQUENCE IF NOT EXISTS services_id_seq;

-- Sequence for nbservices table
CREATE SEQUENCE IF NOT EXISTS nbservices_insertid_seq;

-- Sequence for appointments table
CREATE SEQUENCE IF NOT EXISTS appointments_appointmentid_seq;

-- Sequence for questions table
CREATE SEQUENCE IF NOT EXISTS questions_questionid_seq;

-- Sequence for ratings table
CREATE SEQUENCE IF NOT EXISTS ratings_ratingid_seq;

-- Password Recovery Codes table
CREATE TABLE IF NOT EXISTS passwordrecoverycodes (
    id SERIAL PRIMARY KEY,
    userid INT REFERENCES users(userid) ON DELETE CASCADE NOT NULL UNIQUE,
    code VARCHAR(36) NOT NULL,
    expirationDate TIMESTAMP NOT NULL
);

-- User Verification Codes table
CREATE TABLE IF NOT EXISTS userverificationcodes (
    id SERIAL PRIMARY KEY,
    userid INT REFERENCES users(userid) ON DELETE CASCADE NOT NULL UNIQUE,
    verificationCode VARCHAR(255) NOT NULL,
    expirationDate TIMESTAMP NOT NULL
);

-- Sequence for passwordrecoverycodes table
CREATE SEQUENCE IF NOT EXISTS passwordrecovery_codeid_seq;

-- Sequence for userverificationcodes table
CREATE SEQUENCE IF NOT EXISTS userverification_codeid_seq;
-- insert into users(username, name, surname, email, telephone, password, isprovider) values ('solro', 'sol', 'rodri', 'solrodriguezgiana@gmail.com', '113452343', 'solro', true);
-- insert into business (userid, businessname, businessTelephone, businessEmail, businessLocation) values (1, 'Sol nails shop', '11365335', 'solrodriguezgiana@gmail.com', 'Palermo');
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Uñas capping', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '10000', TRUE);
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Uñas francecita', 'Servicio de uñas francesitas.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '5000', TRUE);
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Limpieza de cuticula', 'Servicio de uñas: limpieza de cuticula express.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '3000', TRUE);
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Ya no se que otro servicio inventar de uñas', 'Servicio de uñas: necesito que este texto sea largo. Chatgpt: La vida es un viaje lleno de sorpresas y aventuras. Cada día es una oportunidad para explorar, aprender y crecer. .', TRUE, 'Palermo', 'Belleza', 60, 'Por hora', '3000', FALSE);
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Uñas capping1', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '10000', TRUE);
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Uñas capping2', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'Palermo', 'Limpieza', 60, 'Por hora', '10000', TRUE);
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Uñas capping3', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '10000', TRUE);
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Uñas capping4', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '10000', TRUE);
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Uñas capping5', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '10000', TRUE);
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Uñas capping6', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '10000', TRUE);
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Uñas capping7', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '10000', TRUE);
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Uñas capping8', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '10000', TRUE);
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Uñas capping9', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'Palermo', 'Limpieza', 60, 'Por hora', '10000', TRUE);
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Uñas capping10', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'Palermo', 'Limpieza', 60, 'Por hora', '10000', TRUE);
-- insert into services(businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1, 'Uñas capping11', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'Palermo', 'Limpieza', 60, 'Por hora', '10000', TRUE);


