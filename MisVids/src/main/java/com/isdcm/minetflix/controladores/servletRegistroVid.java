package com.isdcm.minetflix.controladores;


import com.isdcm.minetflix.dao.VideoDAO;
import com.isdcm.minetflix.model.Video;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class servletRegistroVid extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Comprobar si hay sesión iniciada
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Recoger parámetros
        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");
        String fecha = request.getParameter("fecha");
        String duracion = request.getParameter("duracion");
        String reproduccionesStr = request.getParameter("reproducciones");
        String descripcion = request.getParameter("descripcion");
        String formato = request.getParameter("formato");
        String rutavideo = request.getParameter("rutavideo");

        // Validaciones
        if (titulo == null || titulo.isEmpty() ||
            autor == null || autor.isEmpty() ||
            fecha == null || fecha.isEmpty() ||
            duracion == null || duracion.isEmpty() ||
            reproduccionesStr == null || reproduccionesStr.isEmpty() ||
            descripcion == null || descripcion.isEmpty() ||
            formato == null || formato.isEmpty()) {

            request.setAttribute("mensajeError", "Todos los campos son obligatorios.");
            request.getRequestDispatcher("registroVid.jsp").forward(request, response);
            return;
        }

        int reproducciones = 0;
        try {
            reproducciones = Integer.parseInt(reproduccionesStr);
        } catch (NumberFormatException e) {
            request.setAttribute("mensajeError", "El número de reproducciones debe ser un valor numérico.");
            request.getRequestDispatcher("registroVid.jsp").forward(request, response);
            return;
        }

        // Crear objeto Video
        Video v = new Video(titulo, autor, fecha, duracion, reproducciones, descripcion, formato, rutavideo);

        // Insertar en la base de datos
        try {
            boolean insertado = VideoDAO.insertarVideo(v);
            if (insertado) {
                request.setAttribute("mensajeExito", "Vídeo registrado correctamente.");
            } else {
                request.setAttribute("mensajeError", "El vídeo ya existe o hubo un error.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error al insertar en la base de datos.");
        }

        request.getRequestDispatcher("registroVid.jsp").forward(request, response);
    }
}
