package com.isdcm.minetflix.controladores;

import com.isdcm.minetflix.utils.Utils;
import com.isdcm.minetflix.utils.VideoPlaybackManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "servletStreamVideo", urlPatterns = {"/servletStreamVideo"})
public class servletStreamVideo extends HttpServlet {

    private static final double MIN_PERCENTAGE_PLAYED = 0.10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar si el usuario está autenticado
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String fileName = request.getParameter("file");
        String videoIdParam = request.getParameter("id");

        if (fileName == null || fileName.isEmpty() || videoIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetros 'file' o 'id' faltantes.");
            return;
        }

        int videoId;
        try {
            videoId = Integer.parseInt(videoIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de video inválido.");
            return;
        }

        // Construir la ruta del archivo de video
        File videoFile = new File(Utils.getVideoStoragePath(), fileName);
        if (!videoFile.exists() || !videoFile.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no encontrado.");
            return;
        }

        // Detectar el MIME type del video
        Path filePath = Paths.get(videoFile.getAbsolutePath());
        String mimeType = Files.probeContentType(filePath);
        if (mimeType == null) {
            mimeType = "video/octet-stream"; // Tipo genérico si no se detecta
        }

        // Configurar la respuesta HTTP
        response.setContentType(mimeType);
        response.setContentLengthLong(videoFile.length());
        response.setHeader("Accept-Ranges", "bytes");

        // Streaming de video
        long totalBytes = videoFile.length();
        long bytesReproducidos = 0;
        long minBytesToCount = (long) (totalBytes * MIN_PERCENTAGE_PLAYED); // 10% del total

        boolean contadorIncrementado = false;

        try (FileInputStream fis = new FileInputStream(videoFile);
             OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                bytesReproducidos += bytesRead;

                if (!contadorIncrementado && bytesReproducidos >= minBytesToCount) {
                    contadorIncrementado = true;
                    //VideoPlaybackManager.registrarReproduccion(session, videoId);
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String videoIdParam = request.getParameter("id");
        String reproducido = request.getParameter("reproducido");
        
        // Verificar si el usuario está autenticado
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        if (videoIdParam != null && "true".equals(reproducido)) {
            int videoId;
            try {
                videoId = Integer.parseInt(videoIdParam);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de video inválido.");
                return;
            }

            // Registrar reproducción solo si aún no se ha contado en la sesión
            VideoPlaybackManager.registrarReproduccion(session, videoId);
        }
    }
}