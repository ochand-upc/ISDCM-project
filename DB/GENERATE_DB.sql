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
    ('Oliver', 'Chan', 'oliver.chan@example.com', 'oliverc', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4'),
    ('Carlos', 'Rodriguez', 'carlos.rodriguez@example.com', 'carlosrod', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4');

-- Insertar datos de videos
INSERT INTO "PR2".VIDEOS (TITULO, AUTOR, FECHA, DURACION, REPRODUCCIONES, DESCRIPCION, MIME_TYPE, RUTAVIDEO, TIPO_FUENTE, TAMANO)
VALUES 
    ('Capitán América: un nuevo mundo', 'Marvel', '2023-01-01 10:00:00', 157.709, 3500, 'Captain America: Brave New World o Capitán América: Un Nuevo Mundo en Hispanoamérica es una película de superhéroes estadounidense de 2025, con el personaje de Marvel Comics, Sam Wilson / Capitán América.', 'video/mp4', 'videos/inicial_31f73d73d02994a0f4b7930314632d97e2cc56dca09d32255deef42431982b26', 'LOCAL', 11200000),
    ('Historia de la IA', 'Documentary Channel', '2024-01-01 11:00:00', 0, 1000, 'Documental sobre la inteligencia artificial', 'youtube', 'https://www.youtube.com/embed/WCM0h9TX7cY?si=vrUsnpMBpi2hd3D7', 'YOUTUBE', 0),
    ('El Universo', 'Space Channel', '2025-01-01 13:00:00', 0, 5000, 'Exploración del cosmos', 'youtube', 'https://www.youtube.com/embed/cf4GUMjJn58?si=VPFp-Ovgeh-MkEIq', 'YOUTUBE', 0);