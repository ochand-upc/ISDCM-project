/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.isdcm.minetflix.controladores;

import com.isdcm.minetflix.dao.Videos;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;
import com.isdcm.minetflix.modelo.Video;

public class servletListadoVid extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            List<Video> lista = VideoDAO.listarVideos();
            request.setAttribute("listaVideos", lista);
            request.getRequestDispatcher("listadoVid.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error al recuperar los vídeos.");
            request.getRequestDispatcher("listadoVid.jsp").forward(request, response);
        }
    }
}
