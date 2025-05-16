package com.isdcm.manager;

import com.isdcm.dao.VideoDAO;
import com.isdcm.model.Video;
import com.isdcm.security.AesFileEncryptionService;
import com.isdcm.security.FileEncryptionService;
import com.isdcm.utils.Utils;
import java.io.*;
import java.nio.file.Files;
import jakarta.ws.rs.core.StreamingOutput;
import java.nio.file.Path;
import java.sql.SQLException;

public class VideoPlaybackManager {

    private static VideoPlaybackManager instance;

    private VideoPlaybackManager() {}

    public static synchronized VideoPlaybackManager getInstance() {
        if (instance == null) {
            instance = new VideoPlaybackManager();
        }
        return instance;
    }

    /** Incrementa en BD el contador de vistas. */
    public static boolean registrarVisualizacion(int videoId) {
        try {
            return VideoDAO.incrementarVisualizacion(videoId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Crea un StreamingOutput que desencripta el archivo .enc a un temp,
     * aplica seek+range sobre el plano y lo envía al cliente.
     *
     * @param videoId     ID del vídeo en la BD
     * @param rangeHeader cabecera "Range" opcional
     * @return StreamingOutput con los bytes desencriptados
     * @throws Exception si falla I/O o desencriptado
     */
    public static StreamingOutput streamLocalVideo(int videoId, String rangeHeader) throws Exception {
        
        FileEncryptionService encSvc = new AesFileEncryptionService();
        
        // 1) Carga metadata
        Video v = VideoDAO.obtenerVideoPorId(videoId);
        if (v == null || !"LOCAL".equals(v.getTipoFuente())) {
            throw new IllegalArgumentException("Vídeo no válido");
        }

        // 2) Fichero cifrado
        File encFile = new File(Utils.getVideoStoragePath(), v.getRutaVideo());
        if (!encFile.exists()) {
            throw new IllegalArgumentException("Fichero cifrado no existe");
        }

        // 3) Desencripta todo a un temp
        Path tempPlainPath = Files.createTempFile("decvid-", ".mp4");
        File tempPlainFile = tempPlainPath.toFile();
        try {
            encSvc.decrypt(encFile, tempPlainFile, Utils.getSecretKey());
        } catch (Exception ex) {
            // en caso de fallo, limpiamos y propagamos
            Files.deleteIfExists(tempPlainPath);
            throw ex;
        }

        // 4) Calcular offsets según Range
        long fullLength = tempPlainFile.length();
        long start = 0, end = fullLength - 1;
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] parts = rangeHeader.substring(6).split("-");
            start = Long.parseLong(parts[0]);
            if (parts.length > 1 && !parts[1].isEmpty()) {
                end = Long.parseLong(parts[1]);
            }
            if (end >= fullLength) {
                end = fullLength - 1;
            }
        }
        final long fStart = start;
        final long fEnd   = end;
        final long fChunk = fEnd - fStart + 1;

        // 5) Crear StreamingOutput que lee del temp desencriptado
        return output -> {
            try (RandomAccessFile raf = new RandomAccessFile(tempPlainFile, "r")) {
                raf.seek(fStart);
                byte[] buffer = new byte[4096];
                long sent = 0;
                int read;
                while (sent < fChunk &&
                       (read = raf.read(buffer, 0, (int)Math.min(buffer.length, fChunk - sent)))
                       != -1) {
                    output.write(buffer, 0, read);
                    sent += read;
                }
            } finally {
                // eliminamos el temporal tras el streaming
                Files.deleteIfExists(tempPlainPath);
            }
        };
    }

    /** Detecta el MIME type según el fichero en disco. */
    public static String detectMimeType(int videoId) throws IOException, SQLException {
        Video v = VideoDAO.obtenerVideoPorId(videoId);
        File file = new File(Utils.getVideoStoragePath(), v.getRutaVideo());
        String mime = Files.probeContentType(file.toPath());
        return mime!=null ? mime : "video/octet-stream";
    }

    /** Longitud total del fichero en bytes. */
    public static long getFileLength(int videoId) throws IOException, SQLException {
        Video v = VideoDAO.obtenerVideoPorId(videoId);
        return new File(Utils.getVideoStoragePath(), v.getRutaVideo()).length();
    }
}