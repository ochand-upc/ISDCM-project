package com.isdcm.minetflix.exceptions;

/**
 * Excepción base para errores de reproducción de video
 */
public class VideoPlaybackException extends Exception {
    
    public enum ErrorType {
        VIDEO_NOT_FOUND("Video no encontrado"),
        VIDEO_NOT_ENCRYPTED("Video no está cifrado correctamente"),
        VIDEO_CORRUPTED("Archivo de video corrupto o dañado"),
        DECRYPTION_FAILED("Error al descifrar el video"),
        INVALID_RANGE("Rango de bytes inválido"),
        FILE_ACCESS_ERROR("Error de acceso al archivo"),
        UNSUPPORTED_FORMAT("Formato de video no soportado");
        
        private final String description;
        
        ErrorType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private final ErrorType errorType;
    private final int videoId;
    
    public VideoPlaybackException(ErrorType errorType, int videoId, String message) {
        super(message);
        this.errorType = errorType;
        this.videoId = videoId;
    }
    
    public VideoPlaybackException(ErrorType errorType, int videoId, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.videoId = videoId;
    }
    
    public ErrorType getErrorType() {
        return errorType;
    }
    
    public int getVideoId() {
        return videoId;
    }
    
    public String getUserFriendlyMessage() {
        return String.format("%s (Video ID: %d)", errorType.getDescription(), videoId);
    }
    
    public int getHttpStatusCode() {
        switch (errorType) {
            case VIDEO_NOT_FOUND:
                return 404;
            case INVALID_RANGE:
                return 416; // Range Not Satisfiable
            case VIDEO_NOT_ENCRYPTED:
            case VIDEO_CORRUPTED:
            case DECRYPTION_FAILED:
            case UNSUPPORTED_FORMAT:
                return 422; // Unprocessable Entity
            case FILE_ACCESS_ERROR:
            default:
                return 500; // Internal Server Error
        }
    }
}
