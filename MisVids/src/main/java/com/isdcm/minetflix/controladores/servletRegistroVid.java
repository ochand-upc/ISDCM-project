package com.isdcm.minetflix.controladores;

import com.isdcm.minetflix.dao.VideoDAO;
import com.isdcm.minetflix.model.Video;
import com.isdcm.minetflix.security.AesFileEncryptionService;
import com.isdcm.minetflix.security.FileEncryptionService;
import com.isdcm.minetflix.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.SecretKey;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet(name = "servletRegistroVid", urlPatterns = {"/servletRegistroVid"})
@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024,   // 2MB antes de escribir en disco
    maxFileSize       = 50 * 1024 * 1024,  // 50MB por archivo
    maxRequestSize    = 100 * 1024 * 1024  // 100MB total
)
public class servletRegistroVid extends HttpServlet {
    private static final long MAX_REQUEST_SIZE = 100L * 1024 * 1024;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        if (request.getContentLengthLong() > MAX_REQUEST_SIZE) {
            request.setAttribute("mensajeError", 
                    "La solicitud supera el tamaño máximo permitido (100MB).");
            request.getRequestDispatcher("registroVid.jsp").forward(request, response);
            return;
        }

        // Leer parámetros del formulario
        String titulo      = request.getParameter("titulo").trim();
        String autor       = request.getParameter("autor").trim();
        String fechaStr    = request.getParameter("fecha").trim();
        String descripcion = request.getParameter("descripcion").trim();
        String tipoVideo   = request.getParameter("tipoVideo").trim();

        // Validaciones de campos obligatorios
        if (titulo.isEmpty() || autor.isEmpty() || fechaStr.isEmpty() 
                || descripcion.isEmpty() || tipoVideo.isEmpty()) {
            request.setAttribute("mensajeError", "Todos los campos son obligatorios.");
            request.getRequestDispatcher("registroVid.jsp").forward(request, response);
            return;
        }

        // Validar y formatear fecha
        Date fecha = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            fecha = sdf.parse(fechaStr);
            if (fecha.after(new Date())) {
                request.setAttribute("mensajeError", 
                        "La fecha no puede ser posterior a la fecha actual.");
                request.getRequestDispatcher("registroVid.jsp").forward(request, response);
                return;
            }
        } catch (Exception e) {
            request.setAttribute("mensajeError", "Formato de fecha inválido.");
            request.getRequestDispatcher("registroVid.jsp").forward(request, response);
            return;
        }
        String fechaFormateada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                       .format(fecha);

        // Verificar duplicados en BD
        try {
            if (VideoDAO.existeVideo(titulo, autor)) {
                request.setAttribute("mensajeError", 
                        "Ya existe un video con el mismo título y autor.");
                request.getRequestDispatcher("registroVid.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            request.setAttribute("mensajeError", "Error al verificar la base de datos.");
            request.getRequestDispatcher("registroVid.jsp").forward(request, response);
            return;
        }

        double duracionSegundos = 0;
        String mimeType = "";
        String rutaVideo = "";
        Long tamano = null;
        String tipoFuente = tipoVideo.equals("archivo") ? "LOCAL" : "YOUTUBE";

        if ("archivo".equals(tipoVideo)) {
            Part archivoPart;
            try {
                archivoPart = request.getPart("archivoVideo");
            } catch (Exception e) {
                request.setAttribute("mensajeError", "Error al procesar el archivo.");
                request.getRequestDispatcher("registroVid.jsp").forward(request, response);
                return;
            }
            if (archivoPart == null || archivoPart.getSize() == 0) {
                request.setAttribute("mensajeError", "Debe seleccionar un archivo de video.");
                request.getRequestDispatcher("registroVid.jsp").forward(request, response);
                return;
            }

            // Nombre original y nombre cifrado
            String originalName = Paths.get(archivoPart.getSubmittedFileName())
                                       .getFileName().toString();
            String hashName     = Utils.hashString(autor + "_" + titulo + "_" + originalName);
            String encFileName  = hashName + ".enc";

            // Rutas de almacenamiento
            Path storageDir    = Paths.get(Utils.getVideoStoragePath());
            Files.createDirectories(storageDir);
            Path encFilePath   = storageDir.resolve(encFileName);
            File tempPlainFile = Files.createTempFile("upload-", ".tmp").toFile();

            // Guardar upload en archivo temporal
            try (InputStream is = archivoPart.getInputStream()) {
                Files.copy(is, tempPlainFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // Calcular duración antes de cifrar
            duracionSegundos = Utils.calcularDuracion(tempPlainFile);
            mimeType = Utils.obtenerMimeDesdeRequest(archivoPart);

            // Cifrar temporal → archivo .enc
            FileEncryptionService encSvc = new AesFileEncryptionService();
            SecretKey key = Utils.getSecretKey();
            try {
                encSvc.encrypt(tempPlainFile, encFilePath.toFile(), key);
            } catch (Exception ex) {
                tempPlainFile.delete();
                throw new ServletException("Error cifrando el vídeo", ex);
            } finally {
                tempPlainFile.delete();
            }

            // Metadata del cifrado
            rutaVideo = encFileName;
            tamano    = Files.size(encFilePath);
        }
        else if ("youtube".equals(tipoVideo)) {
            String urlYouTube = request.getParameter("youtubeURL").trim();
            if (!Utils.esLinkValidoYouTube(urlYouTube)) {
                request.setAttribute("mensajeError", "El enlace de YouTube no es válido.");
                request.getRequestDispatcher("registroVid.jsp").forward(request, response);
                return;
            }
            duracionSegundos = Utils.obtenerDuracionDeYouTube(urlYouTube);
            mimeType         = "video/youtube";
            rutaVideo        = urlYouTube;
        }

        // Insertar en la base de datos
        Video video = new Video(
            titulo, autor, fechaFormateada, duracionSegundos,
            0, descripcion, mimeType, rutaVideo, tipoFuente, tamano
        );
        try {
            boolean insertado = VideoDAO.insertarVideo(video);
            if (!insertado) {
                request.setAttribute("mensajeError", "Error al registrar el video.");
                request.getRequestDispatcher("registroVid.jsp").forward(request, response);
                return;
            }
        } catch (Exception e) {
            request.setAttribute("mensajeError", "Error al insertar en la base de datos.");
            request.getRequestDispatcher("registroVid.jsp").forward(request, response);
            return;
        }

        sesion.setAttribute("mensajeExito", "Vídeo registrado correctamente.");
        response.sendRedirect("listadoVid.jsp");
    }
}