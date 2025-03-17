-- Eliminar tablas si existen (Derby NO permite IF EXISTS, así que eliminamos directamente)
DROP TABLE "PR2".USUARIOS;
DROP TABLE "PR2".VIDEOS;

-- Eliminar esquema si no tiene tablas (Derby no permite DROP SCHEMA IF EXISTS)
DROP SCHEMA "PR2" RESTRICT;

-- Crear esquema
CREATE SCHEMA "PR2";

-- Crear tabla de usuarios
CREATE TABLE "PR2".USUARIOS (
    ID INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
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
CREATE TABLE "PR2".VIDEOS (
    ID INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    TITULO VARCHAR(100) NOT NULL,
    AUTOR VARCHAR(100) NOT NULL,
    FECHA VARCHAR(30),
    DURACION DECIMAL(10,3),
    REPRODUCCIONES INTEGER DEFAULT 0,
    DESCRIPCION VARCHAR(255),
    MIME_TYPE VARCHAR(30),
    RUTAVIDEO VARCHAR(255) NOT NULL,
    TIPO_FUENTE VARCHAR(10) NOT NULL CHECK (TIPO_FUENTE IN ('LOCAL', 'YOUTUBE', 'OTRO')),
    TAMANO BIGINT
);

-- Insertar datos de usuarios
INSERT INTO "PR2".USUARIOS (NOMBRE, APELLIDOS, EMAIL, USERNAME, PASSWORD)
VALUES 
    ('Juan', 'Pérez', 'juan.perez@example.com', 'juanp', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4'),
    ('Jose', 'Pérez', 'jose.perez@example.com', 'josep', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4'),
    ('María', 'Gómez', 'maria.gomez@example.com', 'mariag', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4'),
    ('Pedro', 'López', 'pedro.lopez@example.com', 'pedrol', 'pas03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4sPedro'),
    ('Ana', 'Martínez', 'ana.martinez@example.com', 'anam', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4'),
    ('Carlos', 'Sánchez', 'carlos.sanchez@example.com', 'carloss', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4');

-- Insertar datos de videos
INSERT INTO "PR2".VIDEOS (TITULO, AUTOR, FECHA, DURACION, REPRODUCCIONES, DESCRIPCION, MIME_TYPE, RUTAVIDEO, TIPO_FUENTE, TAMANO)
VALUES 
    ('Big Buck Bunny', 'Peach Open Movie', '2023-02-21', 10, 120, 'Cortometraje animado libre', 'video/mp4', '/videos/bigbuckbunny.mp4', 'LOCAL', 0),
    ('Sintel', 'Blender Foundation', '2023-02-20', 1, 80, 'Cortometraje animado', 'video/mp4', '/videos/sintel.mp4', 'LOCAL', 209715200),
    ('Tears of Steel', 'Blender Foundation', '2023-02-19', 15000, 50, 'Cortometraje de ciencia ficción', 'video/mkv', '/videos/tearsofsteel.mkv', 'LOCAL', 0),
    ('Elephants Dream', 'Blender Foundation', '2023-02-18', 10000, 70, 'Cortometraje experimental', 'video/ogg', '/videos/elephantsdream.ogg', 'LOCAL', 0),
    ('Tears of Steel 2', 'Blender Foundation', '2023-02-17', 10000, 35, 'Secuela de ciencia ficción', 'video/mp4', '/videos/tearsofsteel2.mp4', 'LOCAL', 0),
    ('Historia de la IA', 'Documentary Channel', '2023-02-16', 10000, 10, 'Documental sobre la inteligencia artificial', 'youtube', 'https://www.youtube.com/embed/WCM0h9TX7cY?si=vrUsnpMBpi2hd3D7', 'YOUTUBE', 0),
    ('El Universo', 'Space Channel', '2023-02-15', 0, 5, 'Exploración del cosmos', 'youtube', 'https://www.youtube.com/embed/cf4GUMjJn58?si=VPFp-Ovgeh-MkEIq', 'YOUTUBE', 0);