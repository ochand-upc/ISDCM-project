package com.isdcm.minetflix.controladores;

import com.isdcm.minetflix.dao.UsuarioDAO;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.isdcm.minetflix.model.Usuario;
import com.isdcm.minetflix.utils.AppConfig;
import jakarta.json.Json;
import jakarta.json.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
        if (username == null || username.isEmpty()
                || password == null || password.isEmpty()) {
            request.setAttribute("mensajeError", "Todos los campos son obligatorios.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // 1) Hashear la contraseña que llega del formulario
        String hashedPassword = UsuarioDAO.hashPassword(password);

        // Llamada al Web-Service
        URL url = new URL(AppConfig.get("web-service.url") + "/login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        String body = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                username, hashedPassword
        );

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        int code = conn.getResponseCode();

        if (code == 200) {
            JsonObject json = Json.createReader(conn.getInputStream()).readObject();
            String token = json.getString("token");
            // Guardar token en sesión
            request.getSession().setAttribute("jwt", token);
            request.getSession().setAttribute("username", username);
            response.sendRedirect("home.jsp");
        } else {
            request.setAttribute("mensajeError", "Credenciales inválidas");
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

        request.setAttribute("nombre", nombre);
        request.setAttribute("apellidos", apellidos);
        request.setAttribute("email", email);
        request.setAttribute("username", username);

        // Validar campos vacíos o formatos
        if (nombre == null || nombre.isEmpty()
                || apellidos == null || apellidos.isEmpty()
                || email == null || email.isEmpty()
                || username == null || username.isEmpty()
                || password == null || password.isEmpty()
                || password2 == null || password2.isEmpty()) {

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
                request.setAttribute("mensajeError", "Ya existe el usuario " + username + ".");
                request.getRequestDispatcher("registroUsu.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            request.setAttribute("mensajeError", "Ya existe el usuario " + username + ".");
            request.getRequestDispatcher("registroUsu.jsp").forward(request, response);
            return;
        }

        // Verificar si el email ya existe
        try {
            if (UsuarioDAO.existeEmail(email)) {
                request.setAttribute("mensajeError", "Ya existe el email <" + email + ">.");
                request.getRequestDispatcher("registroUsu.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            request.setAttribute("mensajeError", "Ya existe el email <" + email + ">.");
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
                request.setAttribute("mensajeError", "El nombre de usuario ya existe <" + username + ">.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error al registrar usuario en la base de datos.");
        }

        String wsUrl = AppConfig.get("web-service.url") + "/login";
        HttpURLConnection conn = (HttpURLConnection) new URL(wsUrl).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        String payload = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                username, hashedPassword
        );

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            JsonObject json = Json.createReader(conn.getInputStream()).readObject();
            String token = json.getString("token");

            // Guardar token y usuario en sesión
            HttpSession session = request.getSession(true);
            session.setAttribute("jwt", token);
            session.setAttribute("username", username);

            // Redirección al home ya logueado
            response.sendRedirect("home.jsp");
        } else {
            // Redirección a login por error en manejo del token
            request.setAttribute("mensajeError", "Registro OK, pero fallo al iniciar sesión automáticamente.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
