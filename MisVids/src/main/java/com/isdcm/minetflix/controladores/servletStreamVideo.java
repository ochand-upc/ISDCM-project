package com.isdcm.minetflix.controladores;

import com.isdcm.minetflix.dao.VideoDAO;
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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "servletStreamVideo", urlPatterns = {"/servletStreamVideo"})
public class servletStreamVideo extends HttpServlet {

    private static final String VIDEO_STORAGE_PATH = "/opt/uploads/videos/";
    private boolean contadorIncrementado = false;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar si el usuario está autenticado
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String videoId = request.getParameter("id");
        incrementarReproduccionesVideo(videoId);

        // Obtener el nombre del archivo de video
        String fileName = request.getParameter("file");
        if (fileName == null || fileName.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetro 'file' faltante.");
            return;
        }

        // Construir la ruta del archivo de video
        File videoFile = new File(VIDEO_STORAGE_PATH, fileName);
        if (!videoFile.exists() || !videoFile.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no encontrado.");
            return;
        }

        // Detectar el MIME type del video (ejemplo: video/mp4, video/mkv, etc.)
        Path filePath = Paths.get(videoFile.getAbsolutePath());
        String mimeType = Files.probeContentType(filePath);
        if (mimeType == null) {
            mimeType = "video/octet-stream"; // Si no se detecta, se usa un tipo genérico
        }

        // Configurar la respuesta HTTP
        response.setContentType(mimeType);
        response.setContentLengthLong(videoFile.length());
        response.setHeader("Accept-Ranges", "bytes");  // Soporte para streaming progresivo

        // Enviar el video en fragmentos (streaming)
        try (FileInputStream fis = new FileInputStream(videoFile);
             OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }
    
    private void incrementarReproduccionesVideo(String videoId){
        try {
            if (!contadorIncrementado && VideoDAO.incrementarReproducciones(Integer.parseInt(videoId))) {
                contadorIncrementado = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}