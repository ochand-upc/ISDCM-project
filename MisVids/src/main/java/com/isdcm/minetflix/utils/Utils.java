/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.minetflix.utils;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.MovieHeaderBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Utils {
    
    private static final String PROPERTIES_FILE = System.getProperty("config.path");
    private static String videoStoragePath;
    
    static {
        // Bloque estático: se ejecuta una sola vez al cargar la clase
        try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            Properties props = new Properties();
            props.load(input);

            // Lee la clave "videos.path" del archivo
            videoStoragePath = props.getProperty("videos.path");
            if (videoStoragePath == null || videoStoragePath.isEmpty()) {
                // Si no existe la clave, usar un valor por defecto o lanzar excepción
                videoStoragePath = "videos/";  // o lanza un error
            }
        } catch (IOException e) {
            e.printStackTrace();
            // En caso de error, pon un valor por defecto o maneja la excepción
            videoStoragePath = "videos/";
        }
    }
    
    public static String hashString(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }
    
    public static String obtenerMimeDesdeRequest(Part archivoPart) {
        String contentType = archivoPart.getContentType();
        return (contentType != null) ? contentType : "application/octet-stream";
    }
    
    public static double calcularDuracion(File file) {
        if (file == null || !file.exists()) {
            System.out.println("El archivo no existe o es nulo.");
            return 0;
        }
        try {
            // Lee el contenedor MP4 desde el archivo
            MovieBox movie = MP4Util.parseMovie(file);

            // Busca la cabecera de la película "mvhd" dentro del MovieBox
            MovieHeaderBox mvhd = NodeBox.findFirst(movie, MovieHeaderBox.class, "mvhd");
            if (mvhd == null) {
                // Si no encuentra la cabecera, no podemos determinar la duración
                return 0;
            }

            double timescale = mvhd.getTimescale();  // "ticks" por segundo
            double duration = mvhd.getDuration();    // duración en "ticks"
            return duration / timescale;            // duración en segundos
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
        
    public static boolean esLinkValidoYouTube(String url) {
        return url != null && url.matches("^(https?://)?(www\\.)?youtube\\.com/embed/[a-zA-Z0-9_-]{11}([?].*)?$");
    }

    public static Long obtenerDuracionDeYouTube(String url) {
        return Long.valueOf("0");
    }
    
    public static String extraerYouTubeId(String url) {
        String regex = "(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/embed\\/)([a-zA-Z0-9_-]{11})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        return matcher.find() ? matcher.group(1) : "";
    }
    
    public static String getVideoStoragePath() {
        return videoStoragePath;
    }
    
    // Obtener secretKey
    public static SecretKey getSecretKey() {
        byte[] keyBytes = AppConfig.get("encryption.key").getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(Arrays.copyOf(keyBytes, 16), "AES");
    }
}
