package com.isdcm.minetflix.controladores;

import com.isdcm.minetflix.dao.VideoDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.isdcm.minetflix.model.Video;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "servletListadoVid", urlPatterns = {"/servletListadoVid"})
public class servletListadoVid extends HttpServlet {
   
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
       HttpSession sesion = request.getSession(false);
       if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
           response.sendRedirect("login.jsp");
           return;
       }
       
       // 1) Recuperar el mensaje de éxito si existe
       String mensajeExito = (String) sesion.getAttribute("mensajeExito");
       if (mensajeExito != null) {
           // Pasarlo al request
           request.setAttribute("mensajeExito", mensajeExito);
           // Borrarlo de sesión
           sesion.removeAttribute("mensajeExito");
       }

       try {
           // Asegúrate de que VideoDAO.listarVideos() obtenga los datos correctamente
           List<Video> lista = VideoDAO.listarVideos();
           request.setAttribute("listaVideos", lista);  // La lista se pasa como atributo
           request.getRequestDispatcher("listadoVid.jsp").forward(request, response);
       } catch (SQLException e) {
           e.printStackTrace();
           request.setAttribute("mensajeError", "Error al recuperar los vídeos.");
           request.getRequestDispatcher("listadoVid.jsp").forward(request, response);
       }
   }
}
