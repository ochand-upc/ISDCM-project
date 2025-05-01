package com.isdcm.utils;

import com.isdcm.dao.VideoDAO;
import com.isdcm.model.Video;
import java.io.*;
import java.nio.file.Files;
import jakarta.ws.rs.core.StreamingOutput;
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
     * Crea un StreamingOutput que hace seek+range sobre el fichero. 
     */
    public static StreamingOutput streamLocalVideo(int videoId, String rangeHeader)
            throws Exception {
        Video v = VideoDAO.obtenerVideoPorId(videoId);
        if (v == null || !"LOCAL".equals(v.getTipoFuente())) {
            throw new IllegalArgumentException("Vídeo no válido");
        }
        File file = new File(Utils.getVideoStoragePath(), v.getRutaVideo());
        if (!file.exists()) throw new IllegalArgumentException("Fichero no existe");

        long length = file.length();
        long start = 0, end = length - 1;
        if (rangeHeader!=null && rangeHeader.startsWith("bytes=")) {
            String[] parts = rangeHeader.substring(6).split("-");
            start = Long.parseLong(parts[0]);
            if (parts.length>1 && !parts[1].isEmpty()) {
                end = Long.parseLong(parts[1]);
            }
            if (end>=length) end = length-1;
        }

        final long fStart = start;
        final long fEnd   = end;
        final long fChunk = fEnd - fStart + 1;

        return out -> {
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                raf.seek(fStart);
                byte[] buf = new byte[4096];
                long sent = 0;
                int read;
                while (sent < fChunk &&
                       (read = raf.read(buf, 0, (int)Math.min(buf.length, fChunk-sent)))
                       != -1) {
                    out.write(buf, 0, read);
                    sent += read;
                }
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