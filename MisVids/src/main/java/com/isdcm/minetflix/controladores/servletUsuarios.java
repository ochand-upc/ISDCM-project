package com.isdcm.minetflix.controladores;

import com.isdcm.minetflix.dao.UsuarioDAO;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.isdcm.minetflix.model.Usuario;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "servletUsuarios", urlPatterns = {"/servletUsuarios"})
public class servletUsuarios extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if ("login".equals(accion)) {
            procesarLogin(request, response);
        } else if ("registrar".equals(accion)) {
            procesarRegistro(request, response);
        }
    }

    private void procesarLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validar campos vacíos
        if (username == null || username.isEmpty() || 
            password == null || password.isEmpty()) {
            request.setAttribute("mensajeError", "Todos los campos son obligatorios.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        // 1) Hashear la contraseña que llega del formulario
        String hashedPassword = UsuarioDAO.hashPassword(password);

        try {
            boolean valido = UsuarioDAO.validarLogin(username, hashedPassword);
            if (valido) {
                // Crear sesión
                HttpSession sesion = request.getSession(true);
                sesion.setAttribute("usuarioLogueado", username);
                response.sendRedirect("home.jsp");
            } else {
                request.setAttribute("mensajeError", "Credenciales incorrectas.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error de base de datos.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    private void procesarRegistro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String apellidos = request.getParameter("apellidos");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String password2 = request.getParameter("password2");

        // Validar campos vacíos o formatos
        if (nombre == null || nombre.isEmpty() ||
            apellidos == null || apellidos.isEmpty() ||
            email == null || email.isEmpty() ||
            username == null || username.isEmpty() ||
            password == null || password.isEmpty() ||
            password2 == null || password2.isEmpty()) {

            request.setAttribute("mensajeError", "Todos los campos son obligatorios.");
            request.getRequestDispatcher("registroUsu.jsp").forward(request, response);
            return;
        }

        // Validar que las contraseñas coinciden
        if (!password.equals(password2)) {
            request.setAttribute("mensajeError", "Las contraseñas no coinciden.");
            request.getRequestDispatcher("registroUsu.jsp").forward(request, response);
            return;
        }
        
        // Verificar si el username ya existe
        try {
            if (UsuarioDAO.existeUsername(username)) {
                request.setAttribute("mensajeError", "Ya existe el usuario.");
                request.getRequestDispatcher("registroUsu.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            request.setAttribute("mensajeError", "Ya existe el usuario.");
            request.getRequestDispatcher("registroUsu.jsp").forward(request, response);
            return;
        }
        
        // Verificar si el email ya existe
        try {
            if (UsuarioDAO.existeEmail(email)) {
                request.setAttribute("mensajeError", "Ya existe el email.");
                request.getRequestDispatcher("registroUsu.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            request.setAttribute("mensajeError", "Ya existe el email.");
            request.getRequestDispatcher("registroUsu.jsp").forward(request, response);
            return;
        }
        
        // Hashear la contraseña
        String hashedPassword = UsuarioDAO.hashPassword(password);

        // Crear objeto Usuario
        Usuario u = new Usuario(nombre, apellidos, email, username, hashedPassword);
        try {
            boolean insertado = UsuarioDAO.insertarUsuario(u);
            if (insertado) {
                request.setAttribute("mensajeExito", "Usuario registrado correctamente.");
            } else {
                request.setAttribute("mensajeError", "El nombre de usuario ya existe.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error al insertar en la base de datos.");
        }
        // Crear sesión
        HttpSession sesion = request.getSession(true);
        sesion.setAttribute("usuarioLogueado", username);
        request.getRequestDispatcher("home.jsp").forward(request, response);
    }
}
