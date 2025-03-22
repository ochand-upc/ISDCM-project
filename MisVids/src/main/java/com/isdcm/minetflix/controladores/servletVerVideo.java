package com.isdcm.minetflix.controladores;

import com.isdcm.minetflix.dao.VideoDAO;
import com.isdcm.minetflix.model.Video;
import com.isdcm.minetflix.utils.Utils;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;

@WebServlet(name = "servletVerVideo", urlPatterns = {"/servletVerVideo"})
public class servletVerVideo extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String videoId = request.getParameter("id");

        if (videoId == null || videoId.isEmpty()) {
            response.sendRedirect("servletListadoVid");
            return;
        }
          
        try {
            int id = Integer.parseInt(videoId);
            Video video = VideoDAO.obtenerVideoPorId(id);

            if (video == null) {
                request.setAttribute("mensajeError", "El video no existe.");
                request.getRequestDispatcher("servletListadoVid").forward(request, response);
                return;
            }

            // Determinar la URL del video
            String rutaVideo;
            if ("LOCAL".equals(video.getTipoFuente())) {
                String fileName = new File(Utils.getVideoStoragePath() + video.getRutaVideo()).getName();
                rutaVideo = "/MisVids/servletStreamVideo?id="+ video.getId() + "&file=" + fileName;
                request.setAttribute("fileName", fileName);
            } else { // Significa que es de youtube
                rutaVideo = video.getRutaVideo();
            }

            request.setAttribute("video", video);
            request.setAttribute("rutaVideo", rutaVideo);
            request.getRequestDispatcher("verVideo.jsp").forward(request, response);

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error al cargar el video.");
            request.getRequestDispatcher("servletListadoVid").forward(request, response);
        }
    }

 }
