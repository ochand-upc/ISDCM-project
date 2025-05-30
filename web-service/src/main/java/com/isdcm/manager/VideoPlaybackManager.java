package com.isdcm.manager;

import com.isdcm.dao.VideoDAO;
import com.isdcm.exceptions.VideoPlaybackException;
import com.isdcm.exceptions.VideoPlaybackException.ErrorType;
import com.isdcm.model.Video;
import com.isdcm.security.AesFileEncryptionService;
import com.isdcm.security.FileEncryptionService;
import com.isdcm.utils.Utils;
import java.io.*;
import java.nio.file.Files;
import jakarta.ws.rs.core.StreamingOutput;
import java.nio.file.Path;
import java.sql.SQLException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class VideoPlaybackManager {

    private static VideoPlaybackManager instance;

    private VideoPlaybackManager() {}

    public static synchronized VideoPlaybackManager getInstance() {
        if (instance == null) {
            instance = new VideoPlaybackManager();
        }
        return instance;
    }

    /**
     * Valida si un archivo puede ser un video encriptado válido
     */
    private static boolean isValidEncryptedFile(File file) {
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        
        // Un archivo encriptado AES debe tener al menos 32 bytes (IV + algún contenido)
        if (file.length() < 32) {
            return false;
        }
        
        // Verificar que no sea un archivo MP4 plano (detectar header MP4)
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[8];
            if (fis.read(header) == 8) {
                // Detectar signature MP4/MOV
                String headerStr = new String(header, 4, 4);
                if ("ftyp".equals(headerStr)) {
                    return false; // Es un MP4 no encriptado
                }
            }
        } catch (IOException e) {
            // Si no se puede leer, asumir que está encriptado
        }
        
        return true;
    }
    
    /**
     * Detecta el tipo de error durante la desencriptación
     */
    private static ErrorType detectDecryptionErrorType(Exception e, int videoId) {
        String message = e.getMessage();
        
        if (e instanceof BadPaddingException || e instanceof IllegalBlockSizeException) {
            return ErrorType.VIDEO_NOT_ENCRYPTED;
        }
        
        if (message != null) {
            if (message.contains("IV incompleto") || message.contains("invalid key")) {
                return ErrorType.VIDEO_NOT_ENCRYPTED;
            }
            if (message.contains("Input length") || message.contains("padding")) {
                return ErrorType.VIDEO_CORRUPTED;
            }
            if (message.contains("No such file") || message.contains("cannot find")) {
                return ErrorType.VIDEO_NOT_FOUND;
            }
        }
        
        return ErrorType.DECRYPTION_FAILED;
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
     * @throws VideoPlaybackException si hay errores específicos de reproducción
     */
    public static StreamingOutput streamLocalVideo(int videoId, String rangeHeader) throws VideoPlaybackException {
        
        FileEncryptionService encSvc = new AesFileEncryptionService();
        
        try {
            // 1) Carga metadata
            Video v = VideoDAO.obtenerVideoPorId(videoId);
            if (v == null) {
                throw new VideoPlaybackException(ErrorType.VIDEO_NOT_FOUND, videoId, 
                        "Video con ID " + videoId + " no encontrado en la base de datos");
            }
            
            if (!"LOCAL".equals(v.getTipoFuente())) {
                throw new VideoPlaybackException(ErrorType.UNSUPPORTED_FORMAT, videoId, 
                        "Video " + videoId + " no es de tipo LOCAL");
            }

            // 2) Fichero cifrado
            File encFile = new File(Utils.getVideoStoragePath(), v.getRutaVideo());
            if (!encFile.exists()) {
                throw new VideoPlaybackException(ErrorType.VIDEO_NOT_FOUND, videoId, 
                        "Archivo de video " + encFile.getAbsolutePath() + " no encontrado");
            }
            
            // 3) Validar que el archivo parezca estar encriptado
            if (!isValidEncryptedFile(encFile)) {
                if (encFile.length() < 32) {
                    throw new VideoPlaybackException(ErrorType.VIDEO_CORRUPTED, videoId, 
                            "Archivo de video demasiado pequeño: " + encFile.length() + " bytes");
                } else {
                    throw new VideoPlaybackException(ErrorType.VIDEO_NOT_ENCRYPTED, videoId, 
                            "El archivo parece ser un video no encriptado (detectado formato MP4)");
                }
            }

            // 4) Desencripta todo a un temp
            Path tempPlainPath = Files.createTempFile("decvid-" + videoId + "-", ".mp4");
            File tempPlainFile = tempPlainPath.toFile();
            
            try {
                System.out.println("Desencriptando video " + videoId + " de " + encFile.length() + " bytes...");
                encSvc.decrypt(encFile, tempPlainFile, Utils.getSecretKey());
                
                // Validar que la desencriptación produjo un archivo válido
                if (tempPlainFile.length() == 0) {
                    throw new VideoPlaybackException(ErrorType.VIDEO_CORRUPTED, videoId, 
                            "La desencriptación produjo un archivo vacío");
                }
                
                if (tempPlainFile.length() < 1024) { // Muy pequeño para ser un video válido
                    throw new VideoPlaybackException(ErrorType.VIDEO_CORRUPTED, videoId, 
                            "Video desencriptado demasiado pequeño: " + tempPlainFile.length() + " bytes");
                }
                
                System.out.println("Video desencriptado exitosamente: " + tempPlainFile.length() + " bytes");
                
            } catch (VideoPlaybackException e) {
                // Re-lanzar excepciones de video que ya manejamos
                Files.deleteIfExists(tempPlainPath);
                throw e;
            } catch (Exception ex) {
                // Manejar errores de desencriptación
                Files.deleteIfExists(tempPlainPath);
                ErrorType errorType = detectDecryptionErrorType(ex, videoId);
                throw new VideoPlaybackException(errorType, videoId, 
                        "Error durante desencriptación: " + ex.getMessage(), ex);
            }

            // 5) Calcular offsets según Range usando el tamaño REAL del archivo desencriptado
            long fullLength = tempPlainFile.length();
            long start = 0, end = fullLength - 1;
            
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] parts = rangeHeader.substring(6).split("-");
                try {
                    start = Long.parseLong(parts[0]);
                    if (parts.length > 1 && !parts[1].isEmpty()) {
                        end = Math.min(Long.parseLong(parts[1]), fullLength - 1);
                    }
                    if (end >= fullLength) {
                        end = fullLength - 1;
                    }
                    if (start > end || start >= fullLength || start < 0) {
                        Files.deleteIfExists(tempPlainPath);
                        throw new VideoPlaybackException(ErrorType.INVALID_RANGE, videoId, 
                                "Rango inválido: " + rangeHeader + " para archivo de " + fullLength + " bytes");
                    }
                } catch (NumberFormatException e) {
                    Files.deleteIfExists(tempPlainPath);
                    throw new VideoPlaybackException(ErrorType.INVALID_RANGE, videoId, 
                            "Range header mal formado: " + rangeHeader);
                }
            }
            
            final long fStart = start;
            final long fEnd   = end;
            final long fChunk = fEnd - fStart + 1;
            
            System.out.println("Streaming video " + videoId + " exitoso: bytes " + fStart + "-" + fEnd + "/" + fullLength + " (chunk: " + fChunk + ")");

            // 6) Crear StreamingOutput que lee del temp desencriptado con manejo de errores
            return output -> {
                RandomAccessFile raf = null;
                try {
                    raf = new RandomAccessFile(tempPlainFile, "r");
                    raf.seek(fStart);
                    byte[] buffer = new byte[16384];
                    long sent = 0;
                    int read;
                    
                    while (sent < fChunk &&
                           (read = raf.read(buffer, 0, (int)Math.min(buffer.length, fChunk - sent))) != -1) {
                        
                        try {
                            output.write(buffer, 0, read);
                            sent += read;
                            
                            if (sent % (1024 * 1024) == 0) { // Cada 1MB
                                output.flush();
                            }
                            
                        } catch (IOException e) {
                            if (sent > 128 * 1024) {
                                System.out.println("Cliente desconectado durante streaming de video " + videoId + 
                                                 " (enviados " + sent + "/" + fChunk + " bytes)");
                            }
                            break;
                        }
                    }
                    
                    if (sent > 0) {
                        try {
                            output.flush();
                            System.out.println("Streaming de video " + videoId + " completado exitosamente: " + sent + " bytes enviados");
                        } catch (IOException e) {
                            // Ignorar errores de flush final
                        }
                    }
                    
                } catch (IOException e) {
                    System.err.println("Error I/O durante streaming de video " + videoId + ": " + e.getMessage());
                    throw e;
                } finally {
                    if (raf != null) {
                        try {
                            raf.close();
                        } catch (IOException e) {
                            // Silencioso
                        }
                    }
                    
                    try {
                        Files.deleteIfExists(tempPlainPath);
                    } catch (IOException e) {
                        System.err.println("Error eliminando archivo temporal " + tempPlainPath + ": " + e.getMessage());
                    }
                }
            };
            
        } catch (VideoPlaybackException e) {
            // Re-lanzar excepciones que ya manejamos
            throw e;
        } catch (Exception e) {
            // Capturar cualquier otro error no manejado
            throw new VideoPlaybackException(ErrorType.FILE_ACCESS_ERROR, videoId, 
                    "Error inesperado durante el streaming: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene el tamaño real del video desencriptado.
     * Esto requiere desencriptar temporalmente el archivo.
     */
    public static long getPlainVideoLength(int videoId) throws VideoPlaybackException {
        FileEncryptionService encSvc = new AesFileEncryptionService();
        
        try {
            Video v = VideoDAO.obtenerVideoPorId(videoId);
            if (v == null) {
                throw new VideoPlaybackException(ErrorType.VIDEO_NOT_FOUND, videoId, 
                        "Video no encontrado para obtener tamaño");
            }
            
            if (!"LOCAL".equals(v.getTipoFuente())) {
                throw new VideoPlaybackException(ErrorType.UNSUPPORTED_FORMAT, videoId, 
                        "Video no es de tipo LOCAL");
            }

            File encFile = new File(Utils.getVideoStoragePath(), v.getRutaVideo());
            if (!encFile.exists()) {
                throw new VideoPlaybackException(ErrorType.VIDEO_NOT_FOUND, videoId, 
                        "Archivo de video no encontrado");
            }
            
            if (!isValidEncryptedFile(encFile)) {
                throw new VideoPlaybackException(ErrorType.VIDEO_NOT_ENCRYPTED, videoId, 
                        "Archivo no parece estar encriptado correctamente");
            }

            // Desencriptar a temporal para obtener tamaño real
            Path tempPlainPath = Files.createTempFile("size-check-", ".tmp");
            try {
                encSvc.decrypt(encFile, tempPlainPath.toFile(), Utils.getSecretKey());
                long length = tempPlainPath.toFile().length();
                
                if (length == 0) {
                    throw new VideoPlaybackException(ErrorType.VIDEO_CORRUPTED, videoId, 
                            "Desencriptación produjo archivo vacío");
                }
                
                return length;
            } catch (VideoPlaybackException e) {
                throw e;
            } catch (Exception e) {
                ErrorType errorType = detectDecryptionErrorType(e, videoId);
                throw new VideoPlaybackException(errorType, videoId, 
                        "Error obteniendo tamaño del video: " + e.getMessage(), e);
            } finally {
                Files.deleteIfExists(tempPlainPath);
            }
        } catch (VideoPlaybackException e) {
            throw e;
        } catch (Exception e) {
            throw new VideoPlaybackException(ErrorType.FILE_ACCESS_ERROR, videoId, 
                    "Error accediendo al video: " + e.getMessage(), e);
        }
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