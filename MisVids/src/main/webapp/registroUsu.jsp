<%-- 
    Document   : registroUsu
    Created on : 21 feb 2025, 14:29:31
    Author     : alumne
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("usuarioLogueado") != null) {
        response.sendRedirect("home.jsp");
        return;
    }
%>
<html lang="es">
<head>
    <title>Registro de usuarios - MiNetflix</title>
    <!-- Bootstrap 5 CSS (CDN) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" 
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet" href="css/registroUsu.css">
</head>
<body>
    <div class="registro-container">
        <h2>Registro de usuarios</h2>
        <form action="servletUsuarios" method="post">
            <input type="hidden" name="accion" value="registrar" />

            <label>Nombre:</label>
            <input type="text" name="nombre" maxlength="90"
                   value="<%= request.getAttribute("nombre") != null ? request.getAttribute("nombre") : "" %>"
                   required />

            <label>Apellidos:</label>
            <input type="text" name="apellidos" maxlength="90"
                   value="<%= request.getAttribute("apellidos") != null ? request.getAttribute("apellidos") : "" %>"
                   required />

            <label>Correo electrónico:</label>
            <input type="email" name="email" maxlength="90"
                   value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>"
                   required />

            <label>Nombre de usuario:</label>
            <input type="text" name="username" maxlength="25"
                   value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                   required />

            <label>Contraseña:</label>
            <input type="password" name="password" maxlength="50" required />

            <label>Repetir contraseña:</label>
            <input type="password" name="password2" maxlength="50" required />

            <input type="submit" value="Registrar usuario" />
        </form>

        <% if (request.getAttribute("mensajeError") != null) { %>
            <!-- Contenedor para el toast (parte superior derecha) -->
            <div class="position-fixed top-0 end-0 p-3" style="z-index: 9999">
                <!-- Toast en color de fondo rojo (text-bg-danger) -->
                <div id="errorToast" class="toast align-items-center text-bg-danger border-0"
                     role="alert" aria-live="assertive" aria-atomic="true"
                     data-bs-autohide="false">
                    <div class="d-flex">
                        <div class="toast-body">
                            <%= request.getAttribute("mensajeError") %>
                        </div>
                        <button type="button" class="btn-close btn-close-white me-2 m-auto"
                                data-bs-dismiss="toast" aria-label="Close"></button>
                    </div>
                </div>
            </div>
            <!-- Script para inicializar el toast automáticamente -->
            <script>
                document.addEventListener('DOMContentLoaded', function() {
                    var toastEl = document.getElementById('errorToast');
                    var toast = new bootstrap.Toast(toastEl);
                    toast.show();
                });
            </script>
        <% } %>

        <% if (request.getAttribute("mensajeExito") != null) { %>
             <!-- Contenedor para el toast (parte superior derecha) -->
            <div class="position-fixed top-0 end-0 p-3" style="z-index: 9999;">
                <!-- Toast con fondo verde (text-bg-success) -->
                <div id="successToast" class="toast align-items-center text-bg-success border-0"
                     role="alert" aria-live="assertive" aria-atomic="true"
                     data-bs-autohide="false">
                    <div class="d-flex">
                        <div class="toast-body">
                            <%= request.getAttribute("mensajeExito") %>
                        </div>
                        <button type="button" class="btn-close btn-close-white me-2 m-auto"
                                data-bs-dismiss="toast" aria-label="Close"></button>
                    </div>
                </div>
            </div>
            <!-- Script para inicializar el toast automáticamente -->
            <script>
                document.addEventListener('DOMContentLoaded', function() {
                    var toastEl = document.getElementById('successToast');
                    var toast = new bootstrap.Toast(toastEl);
                    toast.show();
                });
            </script>
        <% } %>

        <p><a href="login.jsp">¿Ya tienes cuenta? Inicia sesión aquí</a></p>
    </div>
        
    <!-- Bootstrap 5 JS (CDN) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
            crossorigin="anonymous"></script>
</body>
</html>