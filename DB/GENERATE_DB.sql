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
INSERT INTO "PR2".VIDEOS (
    TITULO, AUTOR, FECHA, DURACION, REPRODUCCIONES,
    DESCRIPCION, MIME_TYPE, RUTAVIDEO, TIPO_FUENTE, TAMANO
)
VALUES
('Video Local 1',  'Director E', '2023-02-02 00:00:00', 158.000, 4123, 'Escenas exclusivas y material inédito.',      'video/mp4', 'inicial_1', 'LOCAL', 10100000),
  ('Video Local 2',  'Creator C',  '2023-03-03 00:00:00', 197.000, 2345, 'Documental informativo para todo público.',      'video/mp4', 'inicial_2', 'LOCAL', 18200000),
  ('Video Local 3',  'Director E', '2023-04-04 00:00:00', 191.000,  785, 'Descripción detallada del contenido del vídeo.',   'video/mp4', 'inicial_3', 'LOCAL', 52800000),
  ('Video Local 4',  'Studio A',   '2023-05-05 00:00:00', 158.000, 1678, 'Video instructivo de alta calidad.',              'video/mp4', 'inicial_1', 'LOCAL', 10100000),
  ('Video Local 5',  'Channel B',  '2023-06-06 00:00:00', 197.000,  942, 'Escenas exclusivas y material inédito.',            'video/mp4', 'inicial_2', 'LOCAL', 18200000),
  ('Video Local 6',  'Creator C',  '2023-07-07 00:00:00', 191.000, 3210, 'Documental informativo para todo público.',       'video/mp4', 'inicial_3', 'LOCAL', 52800000),
  ('Video Local 7',  'Creator C',  '2023-08-08 00:00:00', 158.000, 4893, 'Descripción detallada del contenido del vídeo.',   'video/mp4', 'inicial_1', 'LOCAL', 10100000),
  ('Video Local 8',  'Director E', '2023-09-09 00:00:00', 197.000, 1274, 'Video instructivo de alta calidad.',              'video/mp4', 'inicial_2', 'LOCAL', 18200000),
  ('Video Local 9',  'Producer D', '2023-10-10 00:00:00', 191.000, 2033, 'Escenas exclusivas y material inédito.',            'video/mp4', 'inicial_3', 'LOCAL', 52800000),
  ('Video Local 10', 'Producer D', '2023-11-11 00:00:00',158.000, 3765, 'Documental informativo para todo público.',       'video/mp4', 'inicial_1', 'LOCAL', 10100000),
  ('Video Local 11', 'Studio A',   '2023-12-12 00:00:00',197.000,  899, 'Descripción detallada del contenido del vídeo.',   'video/mp4', 'inicial_2', 'LOCAL', 18200000),
  ('Video Local 12', 'Producer D', '2024-01-13 00:00:00',191.000,  412, 'Video instructivo de alta calidad.',              'video/mp4', 'inicial_3', 'LOCAL', 52800000),
  ('Video Local 13', 'Director E', '2024-02-14 00:00:00',158.000,  324, 'Escenas exclusivas y material inédito.',            'video/mp4', 'inicial_1', 'LOCAL', 10100000),
  ('Video Local 14', 'Creator C',  '2024-03-15 00:00:00',197.000,  285, 'Documental informativo para todo público.',       'video/mp4', 'inicial_2', 'LOCAL', 18200000),
  ('Video Local 15', 'Channel B',  '2024-04-16 00:00:00',191.000,  397, 'Descripción detallada del contenido del vídeo.',   'video/mp4', 'inicial_3', 'LOCAL', 52800000),
  ('Video Local 16', 'Director E', '2024-05-17 00:00:00',158.000, 1042, 'Video instructivo de alta calidad.',              'video/mp4', 'inicial_1', 'LOCAL', 10100000),
  ('Video Local 17', 'Studio A',   '2024-06-18 00:00:00',197.000, 2948, 'Escenas exclusivas y material inédito.',            'video/mp4', 'inicial_2', 'LOCAL', 18200000),
  ('Video Local 18', 'Producer D', '2024-07-19 00:00:00',191.000, 1876, 'Documental informativo para todo público.',       'video/mp4', 'inicial_3', 'LOCAL', 52800000),
  ('Video Local 19', 'Creator C',  '2024-08-20 00:00:00',158.000, 3417, 'Descripción detallada del contenido del vídeo.',   'video/mp4', 'inicial_1', 'LOCAL', 10100000),
  ('Video Local 20', 'Channel B',  '2024-09-21 00:00:00',197.000, 1124, 'Video instructivo de alta calidad.',              'video/mp4', 'inicial_2', 'LOCAL', 18200000),
  ('Tutorial YouTube 1', 'YT Channel 1', '2024-02-02 00:00:00', 0, 14235, 'Escenas exclusivas y material inédito.', 'youtube', 'https://www.youtube.com/embed/WCM0h9TX7cY', 'YOUTUBE', 0),
  ('Tutorial YouTube 2', 'YT Channel 2', '2024-03-03 00:00:00', 0,  8342, 'Video instructivo de alta calidad.',    'youtube', 'https://www.youtube.com/embed/SJb3upF3WiU', 'YOUTUBE', 0),
  ('Tutorial YouTube 3', 'YT Channel 3', '2024-04-04 00:00:00', 0, 19508, 'Documental informativo para todo público.', 'youtube', 'https://www.youtube.com/embed/4RlO0b0kXIU', 'YOUTUBE', 0),
  ('Tutorial YouTube 4', 'YT Channel 4', '2024-05-05 00:00:00', 0, 11234, 'Descripción detallada del contenido del vídeo.', 'youtube', 'https://www.youtube.com/embed/cf4GUMjJn58', 'YOUTUBE', 0),
  ('Tutorial YouTube 5', 'YT Channel 5', '2024-06-06 00:00:00', 0,  9087, 'Escenas exclusivas y material inédito.', 'youtube', 'https://www.youtube.com/embed/dGcsHMXbSOA', 'YOUTUBE', 0),
  ('Tutorial YouTube 6', 'YT Channel 6', '2024-07-07 00:00:00', 0, 14753, 'Video instructivo de alta calidad.',    'youtube', 'https://www.youtube.com/embed/1BX7j2KqFck', 'YOUTUBE', 0),
  ('Tutorial YouTube 7', 'YT Channel 7', '2024-08-08 00:00:00', 0, 12345, 'Documental informativo para todo público.', 'youtube', 'https://www.youtube.com/embed/nPt8bK2gbaU', 'YOUTUBE', 0),
  ('Tutorial YouTube 8', 'YT Channel 8', '2024-09-09 00:00:00', 0,  6789, 'Descripción detallada del contenido del vídeo.', 'youtube', 'https://www.youtube.com/embed/3fumBcKC6RE', 'YOUTUBE', 0),
  ('Tutorial YouTube 9', 'YT Channel 9', '2024-10-10 00:00:00', 0, 20222, 'Escenas exclusivas y material inédito.', 'youtube', 'https://www.youtube.com/embed/5qap5aO4i9A', 'YOUTUBE', 0),
  ('Tutorial YouTube 10','YT Channel 10','2024-11-11 00:00:00', 0, 15890, 'Video instructivo de alta calidad.',    'youtube', 'https://www.youtube.com/embed/e-ORhEE9VVg', 'YOUTUBE', 0),
  ('Tutorial YouTube 11','YT Channel 1', '2024-12-12 00:00:00', 0,  9876, 'Documental informativo para todo público.', 'youtube', 'https://www.youtube.com/embed/WCM0h9TX7cY', 'YOUTUBE', 0),
  ('Tutorial YouTube 12','YT Channel 2', '2024-01-13 00:00:00', 0, 14611, 'Descripción detallada del contenido del vídeo.', 'youtube', 'https://www.youtube.com/embed/SJb3upF3WiU', 'YOUTUBE', 0),
  ('Tutorial YouTube 13','YT Channel 3', '2024-02-14 00:00:00', 0, 13542, 'Escenas exclusivas y material inédito.', 'youtube', 'https://www.youtube.com/embed/4RlO0b0kXIU', 'YOUTUBE', 0),
  ('Tutorial YouTube 14','YT Channel 4', '2024-03-15 00:00:00', 0, 11111, 'Video instructivo de alta calidad.',    'youtube', 'https://www.youtube.com/embed/cf4GUMjJn58', 'YOUTUBE', 0),
  ('Tutorial YouTube 15','YT Channel 5', '2024-04-16 00:00:00', 0, 19876, 'Documental informativo para todo público.', 'youtube', 'https://www.youtube.com/embed/dGcsHMXbSOA', 'YOUTUBE', 0),
  ('Tutorial YouTube 16','YT Channel 6', '2024-05-17 00:00:00', 0, 10432, 'Descripción detallada del contenido del vídeo.', 'youtube', 'https://www.youtube.com/embed/1BX7j2KqFck', 'YOUTUBE', 0),
  ('Tutorial YouTube 17','YT Channel 7', '2024-06-18 00:00:00', 0, 11222, 'Escenas exclusivas y material inédito.', 'youtube', 'https://www.youtube.com/embed/nPt8bK2gbaU', 'YOUTUBE', 0),
  ('Tutorial YouTube 18','YT Channel 8', '2024-07-19 00:00:00', 0, 14321, 'Video instructivo de alta calidad.',    'youtube', 'https://www.youtube.com/embed/3fumBcKC6RE', 'YOUTUBE', 0),
  ('Tutorial YouTube 19','YT Channel 9', '2024-08-20 00:00:00', 0, 13210, 'Descripción detallada del contenido del vídeo.', 'youtube', 'https://www.youtube.com/embed/5qap5aO4i9A', 'YOUTUBE', 0),
  ('Tutorial YouTube 20','YT Channel 10','2024-09-21 00:00:00', 0, 12789, 'Documental informativo para todo público.', 'youtube', 'https://www.youtube.com/embed/e-ORhEE9VVg', 'YOUTUBE', 0)
;