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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
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
            // Comando ffprobe para obtener la duración del video en segundos
            ProcessBuilder processBuilder = new ProcessBuilder(
                "ffprobe", "-i", file.getAbsolutePath(),
                "-show_entries", "format=duration",
                "-v", "quiet",
                "-of", "csv=p=0"
            );

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String durationString = reader.readLine();
            process.waitFor();

            if (durationString != null && !durationString.isEmpty()) {
                return Double.parseDouble(durationString);
            }
        } catch (IOException | InterruptedException | NumberFormatException e) {
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
}
