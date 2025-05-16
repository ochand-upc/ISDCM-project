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
  ('Captain America: Brave New World Trailer 1', 'Marvel Studios',       '2025-01-05 10:00:00', 158.000, 3510, 'Trailer oficial de Captain America: Brave New World',                'video/mp4', 'inicial_1.enc', 'LOCAL', 10100000),
  ('Atlético vs Barça 2-4 Highlights 1',            'LaLiga Highlights',    '2025-01-06 11:00:00', 197.000, 3015, 'Resumen del partido Atlético de Madrid 2-4 Barcelona (1)',           'video/mp4', 'inicial_2.enc', 'LOCAL', 18200000),
  ('Atlético vs Barça 2-4 HD 1',                   'LaLiga HD',            '2025-01-07 12:00:00', 191.000, 5020, 'Resumen en alta definición del partido Barça vs Atlético (1)',     'video/mp4', 'inicial_3.enc', 'LOCAL', 52800000),
  ('Captain America: Brave New World Trailer 2', 'Marvel Studios',       '2025-02-05 10:00:00', 158.000, 3520, 'Trailer oficial de Captain America: Brave New World',                'video/mp4', 'inicial_1.enc', 'LOCAL', 10100000),
  ('Atlético vs Barça2-4 Highlights 2',          'LaLiga Highlights',    '2025-02-06 11:00:00', 197.000, 3030, 'Resumen del partido Atlético de Madrid 2-4 Barcelona (2)',           'video/mp4', 'inicial_2.enc', 'LOCAL', 18200000),
  ('Atlético vs Barça 2-4 HD 2',                  'LaLiga HD',            '2025-02-07 12:00:00', 191.000, 5040, 'Resumen en alta definición del partido Barça vs Atlético (2)',     'video/mp4', 'inicial_3.enc', 'LOCAL', 52800000),
  ('Captain America: Brave New World Trailer 3', 'Marvel Studios',       '2025-03-05 10:00:00', 158.000, 3530, 'Trailer oficial de Captain America: Brave New World',                'video/mp4', 'inicial_1.enc', 'LOCAL', 10100000),
  ('Atlético vs Barça 2-4 Highlights 3',          'LaLiga Highlights',    '2025-03-06 11:00:00', 197.000, 3045, 'Resumen del partido Atlético de Madrid 2-4 Barcelona (3)',           'video/mp4', 'inicial_2.enc', 'LOCAL', 18200000),
  ('Atlético vs Barça 2-4 HD 3',                  'LaLiga HD',            '2025-03-07 12:00:00', 191.000, 5060, 'Resumen en alta definición del partido Barça vs Atlético (3)',     'video/mp4', 'inicial_3.enc', 'LOCAL', 52800000),
  ('Captain America: Brave New World Trailer 4', 'Marvel Studios',       '2025-04-05 10:00:00', 158.000, 3540, 'Trailer oficial de Captain America: Brave New World',                'video/mp4', 'inicial_1.enc', 'LOCAL', 10100000),
  ('Atlético vs Barça 2-4 Highlights 4',          'LaLiga Highlights',    '2025-04-06 11:00:00', 197.000, 3060, 'Resumen del partido Atlético de Madrid 2-4 Barcelona (4)',           'video/mp4', 'inicial_2.enc', 'LOCAL', 18200000),
  ('Atlético vs Barça 2-4 HD 4',                  'LaLiga HD',            '2025-04-07 12:00:00', 191.000, 5080, 'Resumen en alta definición del partido Barça vs Atlético (4)',     'video/mp4', 'inicial_3.enc', 'LOCAL', 52800000),
  ('Captain America: Brave New World Trailer 5', 'Marvel Studios',       '2025-05-05 10:00:00', 158.000, 3550, 'Trailer oficial de Captain America: Brave New World',                'video/mp4', 'inicial_1.enc', 'LOCAL', 10100000),
  ('Atlético vs Barça 2-4 Highlights 5',          'LaLiga Highlights',    '2025-05-06 11:00:00', 197.000, 3075, 'Resumen del partido Atlético de Madrid 2-4 Barcelona (5)',           'video/mp4', 'inicial_2.enc', 'LOCAL', 18200000),
  ('Atlético vs Barça 2-4 HD 5',                  'LaLiga HD',            '2025-05-07 12:00:00', 191.000, 5100, 'Resumen en alta definición del partido Barça vs Atlético (5)',     'video/mp4', 'inicial_3.enc', 'LOCAL', 52800000),
  ('Captain America: Brave New World Trailer 6', 'Marvel Studios',       '2025-06-05 10:00:00', 158.000, 3560, 'Trailer oficial de Captain America: Brave New World',                'video/mp4', 'inicial_1.enc', 'LOCAL', 10100000),
  ('Atlético vs Barça 2-4 Highlights 6',          'LaLiga Highlights',    '2025-06-06 11:00:00', 197.000, 3090, 'Resumen del partido Atlético de Madrid 2-4 Barcelona (6)',           'video/mp4', 'inicial_2.enc', 'LOCAL', 18200000),
  ('Atlético vs Barça 2-4 HD 6',                  'LaLiga HD',            '2025-06-07 12:00:00', 191.000, 5120, 'Resumen en alta definición del partido Barça vs Atlético (6)',     'video/mp4', 'inicial_3.enc', 'LOCAL', 52800000),
  ('Captain America: Brave New World Trailer 7', 'Marvel Studios',       '2025-07-05 10:00:00', 158.000, 3570, 'Trailer oficial de Captain America: Brave New World',                'video/mp4', 'inicial_1.enc', 'LOCAL', 10100000),
  ('Atlético vs Barça 2-4 Highlights 7',          'LaLiga Highlights',    '2025-07-06 11:00:00', 197.000, 3105, 'Resumen del partido Atlético de Madrid 2-4 Barcelona (7)',           'video/mp4', 'inicial_2.enc', 'LOCAL', 18200000),
  ('Spider-Man: No Way Home – Official Trailer',       'Marvel Entertainment',    '2024-02-01 00:00:00', 0, 5123456, 'The official trailer for Spider-Man: No Way Home',               'youtube', 'https://www.youtube.com/embed/JfVOs4VSpmA', 'YOUTUBE', 0),
  ('The Batman – Official Trailer',                    'Warner Bros. Pictures',   '2024-03-05 00:00:00', 0, 4789123, 'The official trailer for The Batman starring Robert Pattinson',  'youtube', 'https://www.youtube.com/embed/mqqft2x_Aa4', 'YOUTUBE', 0),
  ('Avengers: Endgame – Trailer',                      'Marvel Entertainment',    '2024-01-10 00:00:00', 0, 6234100, 'Avengers assemble one last time in Endgame',                    'youtube', 'https://www.youtube.com/embed/TcMBFSGVi1c', 'YOUTUBE', 0),
  ('Tenet – Trailer',                                  'Warner Bros. Pictures',   '2024-02-20 00:00:00', 0, 3123450, 'Christopher Nolan’s mind-bending thriller Tenet',              'youtube', 'https://www.youtube.com/embed/L3pk_TBkihU', 'YOUTUBE', 0),
  ('Joker – Official Trailer',                         'Warner Bros. Pictures',   '2024-01-15 00:00:00', 0, 2890456, 'Joaquin Phoenix stars in Joker',                                'youtube', 'https://www.youtube.com/embed/zAGVQLHvwOY', 'YOUTUBE', 0),
  ('Star Wars: The Rise of Skywalker – Trailer',       'Lucasfilm',               '2024-02-28 00:00:00', 0, 4023123, 'The final chapter of the Skywalker saga',                      'youtube', 'https://www.youtube.com/embed/8Qn_spdM5Zg', 'YOUTUBE', 0),
  ('Black Panther – Official Trailer',                 'Marvel Entertainment',    '2024-03-12 00:00:00', 0, 3589000, 'Black Panther returns to Wakanda',                             'youtube', 'https://www.youtube.com/embed/xjDjIWPwcPU', 'YOUTUBE', 0),
  ('Dune – Trailer',                                   'Warner Bros. Pictures',   '2024-04-01 00:00:00', 0, 4456789, 'Denis Villeneuve’s epic adaptation of Dune',                   'youtube', 'https://www.youtube.com/embed/n9xhJrPXop4', 'YOUTUBE', 0),
  ('No Time to Die – Official Trailer',                'MGM',                     '2024-03-22 00:00:00', 0, 3987123, 'Daniel Craig’s final outing as James Bond',                    'youtube', 'https://www.youtube.com/embed/BIhNsAtPbPI', 'YOUTUBE', 0),
  ('Top Gun: Maverick – Trailer',                      'Paramount Pictures',      '2024-04-10 00:00:00', 0, 5250000, 'Tom Cruise returns in Top Gun: Maverick',                      'youtube', 'https://www.youtube.com/embed/giXco2jaZ_4', 'YOUTUBE', 0),
  ('Justice League – Trailer',                         'Warner Bros. Pictures',   '2024-02-18 00:00:00', 0, 3120000, 'The epic team-up of DC’s greatest heroes',                    'youtube', 'https://www.youtube.com/embed/SWWcqtm8SPY', 'YOUTUBE', 0),
  ('Jurassic World – Official Trailer',                'Universal Pictures',      '2024-01-25 00:00:00', 0, 2754321, 'Welcome to Jurassic World',                                     'youtube', 'https://www.youtube.com/embed/RFinNxS5KN4', 'YOUTUBE', 0),
  ('Inception – Trailer',                              'Warner Bros. Pictures',   '2024-03-02 00:00:00', 0, 2987654, 'Your mind is the scene of the crime',                          'youtube', 'https://www.youtube.com/embed/8hP9D6kZseM', 'YOUTUBE', 0),
  ('The Dark Knight – Trailer',                        'Warner Bros. Pictures',   '2024-02-08 00:00:00', 0, 3245000, 'Witness the rise of the Dark Knight',                          'youtube', 'https://www.youtube.com/embed/kmJLuwP3MbY', 'YOUTUBE', 0),
  ('Interstellar – Trailer',                           'Paramount Pictures',      '2024-01-30 00:00:00', 0, 2867000, 'Mankind was born on Earth. It was never meant to die here.',   'youtube', 'https://www.youtube.com/embed/zSWdZVtXT7E', 'YOUTUBE', 0),
  ('The Godfather – Trailer',                          'Paramount Pictures',      '2024-02-12 00:00:00', 0, 1987000, 'An offer you can’t refuse.',                                   'youtube', 'https://www.youtube.com/embed/sY1S34973zA', 'YOUTUBE', 0),
  ('Titanic – Trailer',                                'Paramount Pictures',      '2024-03-18 00:00:00', 0, 2150000, 'Nothing on Earth could come between them.',                    'youtube', 'https://www.youtube.com/embed/kVrqfYjkTdQ', 'YOUTUBE', 0),
  ('Avatar – Official Trailer',                        '20th Century Studios',    '2024-04-05 00:00:00', 0, 4321000, 'Enter the world of Pandora',                                   'youtube', 'https://www.youtube.com/embed/5PSNL1qE6VY', 'YOUTUBE', 0),
  ('The Matrix – Trailer',                             'Warner Bros. Pictures',   '2024-02-25 00:00:00', 0, 3198000, 'Welcome to the Real World',                                    'youtube', 'https://www.youtube.com/embed/m8e-FF8MsqU', 'YOUTUBE', 0),
  ('Forrest Gump – Trailer',                           'Paramount Pictures',      '2024-03-28 00:00:00', 0, 2543000, 'Life is like a box of chocolates.',                            'youtube', 'https://www.youtube.com/embed/eCdq-hjSTOc', 'YOUTUBE', 0
);