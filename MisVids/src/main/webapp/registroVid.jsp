<%-- 
    Document   : registroVid
    Created on : 21 feb 2025, 14:29:58
    Author     : alumne
--%>

<%@ page session="true" %>
<%
    // Comprobamos si existe sesión
    if (session.getAttribute("usuarioLogueado") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<html lang="es">
<head>
    <title>Registro de Videos - MiNetflix</title>
    <!-- Bootstrap 5 CSS (CDN) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" 
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet" href="css/registroVid.css">
</head>
<body>
    <div class="registro-container">
        <h2>Registrar nuevo video</h2>
        <form action="servletRegistroVid" method="post">
            <label>Título:</label>
            <input type="text" name="titulo" required />

            <label>Autor:</label>
            <input type="text" name="autor" required />

            <label>Fecha:</label>
            <input type="date" name="fecha" required />
            
            <label>Ruta</label>
            <input type="text" name="rutaVideo" placeholder="/path_to_video/video.mp4" required />

            <label>Duración:</label>
            <input type="text" name="duracion" placeholder="Ej. 2h 15min" required />

            <label>Reproducciones:</label>
            <input type="number" name="reproducciones" min="0" value="0" required />

            <label>Descripción:</label>
            <textarea name="descripcion" placeholder="Añade una breve descripción del video"></textarea>

            <label>Formato:</label>
            <select name="formato" required>
                <option value="">Seleccione un formato</option>
                <option value="MP4">MP4</option>
                <option value="AVI">AVI</option>
                <option value="MKV">MKV</option>
                <option value="MOV">MOV</option>
                <option value="WMV">WMV</option>
                <option value="FLV">FLV</option>
            </select>

            <input type="submit" value="Registrar Video" />
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

        <a href="home.jsp" class="back-link">Volver al menú principal</a>
    </div>
        
    <!-- Bootstrap 5 JS (CDN) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
            crossorigin="anonymous"></script>
</body>
</html>