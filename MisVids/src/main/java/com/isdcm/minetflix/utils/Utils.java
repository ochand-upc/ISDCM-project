/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.minetflix.utils;

import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class Utils {
    
    private static final String VIDEO_STORAGE_PATH = "/opt/uploads/videos/";
    
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
            // Crea un objeto FFprobe sin especificar rutas locales
            FFprobe ffprobe = new FFprobe();
            FFmpegProbeResult probeResult = ffprobe.probe(file.getAbsolutePath());
            
            // Retorna la duración en segundos
            return probeResult.getFormat().duration;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
        
    public static boolean esLinkValidoYouTube(String url) {
        return url.matches("^(https?://)?(www\\.)?(youtube|youtu|youtube-nocookie)\\.com/.+$");
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
        return VIDEO_STORAGE_PATH;
    }
}
