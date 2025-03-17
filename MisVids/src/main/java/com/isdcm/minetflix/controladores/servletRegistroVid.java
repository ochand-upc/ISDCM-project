package com.isdcm.minetflix.controladores;

import com.isdcm.minetflix.dao.VideoDAO;
import com.isdcm.minetflix.model.Video;
import com.isdcm.minetflix.utils.Utils;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB antes de escribir en disco
    maxFileSize = 1024 * 1024 * 50, // 50MB tamaño máximo por archivo
    maxRequestSize = 1024 * 1024 * 100 // 100MB tamaño total de la request
)
@WebServlet(name = "servletRegistroVid", urlPatterns = {"/servletRegistroVid"})
public class servletRegistroVid extends HttpServlet {
    
    private static final String VIDEO_STORAGE_PATH = "/opt/uploads/videos/";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String titulo = request.getParameter("titulo").trim();
        String autor = request.getParameter("autor").trim();
        String fechaStr = request.getParameter("fecha").trim();
        String descripcion = request.getParameter("descripcion").trim();
        String tipoVideo = request.getParameter("tipoVideo").trim();
        
        request.setAttribute("titulo", titulo);
        request.setAttribute("autor", autor);
        request.setAttribute("fecha", fechaStr);
        request.setAttribute("descripcion", descripcion);

        // Validaciones básicas
        if (titulo.isEmpty() || autor.isEmpty() || fechaStr.isEmpty() || descripcion.isEmpty() || tipoVideo.isEmpty()) {
            request.setAttribute("mensajeError", "Todos los campos son obligatorios.");
            request.getRequestDispatcher("registroVid.jsp").forward(request, response);
            return;
        }

        // Validar fecha
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = new Date();
        if (fecha.after(new Date())) {
            request.setAttribute("mensajeError", "La fecha no puede ser posterior a la fecha actual.");
            request.getRequestDispatcher("registroVid.jsp").forward(request, response);
            return;
        }
        
        // Convertir fecha en formato
        SimpleDateFormat formatoFechaDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaFormateada = formatoFechaDB.format(fecha);

        // Convertir tipoVideo a tipoFuente esperado por la BD
        String tipoFuente = tipoVideo.equals("archivo") ? "LOCAL" : "YOUTUBE";

        // Verificar si el video ya existe en la base de datos
        try {
            if (VideoDAO.existeVideo(titulo, autor)) {
                request.setAttribute("mensajeError", "Ya existe un video con el mismo título y autor.");
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
        Long tamano = null;  // Puede ser NULL para videos de YouTube

        if ("archivo".equals(tipoVideo)) {
            Part archivoPart = request.getPart("archivoVideo");
            if (archivoPart == null || archivoPart.getSize() == 0) {
                request.setAttribute("mensajeError", "Debe seleccionar un archivo de video.");
                request.getRequestDispatcher("registroVid.jsp").forward(request, response);
                return;
            }

            // Obtener la extensión del archivo
            String nombreOriginal = Paths.get(archivoPart.getSubmittedFileName()).getFileName().toString();

            // Crear un nombre de archivo único 
            String nombreArchivo = autor.replaceAll("\\s+", "_") + "_" + titulo.replaceAll("\\s+", "_") + "_" + nombreOriginal;

            // Definir la ruta donde se guardará el archivo
            Path rutaDestino = Paths.get(VIDEO_STORAGE_PATH, Utils.hashString(nombreArchivo));

            // Asegurarse de que la carpeta existe
            File directorio = new File(VIDEO_STORAGE_PATH);
            if (!directorio.exists()) {
                directorio.mkdirs();  // Crear la carpeta si no existe
            }

            // Guardar el archivo en el servidor
            try (InputStream input = archivoPart.getInputStream()) {
                Files.copy(input, rutaDestino, StandardCopyOption.REPLACE_EXISTING);
            }

            // Obtener datos del archivo
            File videoFile = rutaDestino.toFile();
            duracionSegundos = Utils.calcularDuracion(videoFile);
            mimeType = Utils.obtenerMimeDesdeRequest(archivoPart);
            rutaVideo = rutaDestino.toString();
            tamano = archivoPart.getSize();
            
        } else if ("youtube".equals(tipoVideo)) {
            String urlYouTube = request.getParameter("youtubeURL").trim();
            if (!Utils.esLinkValidoYouTube(urlYouTube)) {
                request.setAttribute("mensajeError", "El enlace de YouTube no es válido.");
                request.getRequestDispatcher("registroVid.jsp").forward(request, response);
                return;
            }
            duracionSegundos = Utils.obtenerDuracionDeYouTube(urlYouTube);
            mimeType = tipoVideo;
            rutaVideo = urlYouTube;
        }

        // Insertar en la base de datos
        Video v = new Video(titulo, autor, fechaFormateada, duracionSegundos, 0, descripcion, mimeType, rutaVideo, tipoFuente, tamano);
        try {
            boolean insertado = VideoDAO.insertarVideo(v);
            if (!insertado) {
                if ("archivo".equals(tipoVideo)) {
                    Files.deleteIfExists(Paths.get(rutaVideo));
                }
                request.setAttribute("mensajeError", "Hubo un error al registrar el video.");
                request.getRequestDispatcher("registroVid.jsp").forward(request, response);
                return;
            }
        } catch (Exception e) {
            if ("archivo".equals(tipoVideo)) {
                Files.deleteIfExists(Paths.get(rutaVideo));
            }
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error al insertar en la base de datos.");
            request.getRequestDispatcher("registroVid.jsp").forward(request, response);
            return;
        }

        sesion.setAttribute("mensajeExito", "Vídeo registrado correctamente.");
        response.sendRedirect("servletListadoVid");
    }
    
}