DROP SCHEMA IF EXISTS "PR2" CASCADE;

-- Crear esquema pr2
CREATE SCHEMA "PR2";

-- Crear tabla de videos
CREATE TABLE "PR2".USUARIOS
(
    ID INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    NOMBRE VARCHAR(100),
    APELLIDOS VARCHAR(100),
    EMAIL VARCHAR(100),
    USERNAME VARCHAR(50) NOT NULL,
    PASSWORD VARCHAR(50) NOT NULL
);

ALTER TABLE "PR2".USUARIOS
ALTER COLUMN "PASSWORD"
SET DATA TYPE VARCHAR(500);


-- Crear tabla de videos
CREATE TABLE "PR2".VIDEOS
(
    ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    TITULO VARCHAR(100) NOT NULL,
    AUTOR VARCHAR(100) NOT NULL,
    FECHA VARCHAR(10),
    DURACION VARCHAR(10),
    REPRODUCCIONES INTEGER,
    DESCRIPCION VARCHAR(255),
    FORMATO VARCHAR(10),
    RUTAVIDEO VARCHAR(255),
    PRIMARY KEY (ID)
);

-- Insertar datos de usuarios
INSERT INTO "PR2".USUARIOS (NOMBRE, APELLIDOS, EMAIL, USERNAME, PASSWORD)
VALUES 
('Juan', 'Pérez', 'juan.perez@example.com', 'juanp', '1234'),
('María', 'Gómez', 'maria.gomez@example.com', 'mariag', 'maria123'),
('Pedro', 'López', 'pedro.lopez@example.com', 'pedrol', 'passPedro'),
('Ana', 'Martínez', 'ana.martinez@example.com', 'anam', 'anaPass'),
('Carlos', 'Sánchez', 'carlos.sanchez@example.com', 'carloss', 'carlosPass');

-- Insertar datos de videos
INSERT INTO "PR2".VIDEOS (TITULO, AUTOR, FECHA, DURACION, REPRODUCCIONES, DESCRIPCION, FORMATO, RUTAVIDEO)
VALUES 
('Big Buck Bunny', 'Peach Open Movie', '2023-02-21', '09:56', 120, 'Cortometraje animado libre', 'mp4', '/videos/bigbuckbunny.mp4'),
('Sintel', 'Blender Foundation', '2023-02-20', '14:48', 80, 'Cortometraje animado', 'mp4', '/videos/sintel.mp4'),
('Tears of Steel', 'Blender Foundation', '2023-02-19', '12:14', 50, 'Cortometraje de ciencia ficción', 'mkv', '/videos/tearsofsteel.mkv'),
('Elephants Dream', 'Blender Foundation', '2023-02-18', '10:53', 70, 'Cortometraje experimental', 'ogg', '/videos/elephantsdream.ogg'),
('Tears of Steel 2', 'Blender Foundation', '2023-02-17', '13:14', 35, 'Secuela de ciencia ficción', 'mp4', '/videos/tearsofsteel2.mp4');