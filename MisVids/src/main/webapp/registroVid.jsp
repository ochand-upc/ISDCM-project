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
            <form action="servletRegistroVid" method="post" enctype="multipart/form-data">   
                <label>Título:</label>
                <input type="text" name="titulo" 
                       value="<%= request.getAttribute("titulo") != null ? request.getAttribute("titulo") : "" %>"
                       required />

                <label>Autor:</label>
                <input type="text" name="autor" 
                       value="<%= request.getAttribute("autor") != null ? request.getAttribute("autor") : "" %>"
                       required />

                <label>Fecha:</label>
                <input type="date" name="fecha"
                       value="<%= request.getAttribute("fecha") != null ? request.getAttribute("fecha") : "" %>"
                       required />

                <label>Descripción:</label>
                <textarea name="descripcion" 
                          value="<%= request.getAttribute("descripcion") != null ? request.getAttribute("descripcion") : "" %>"
                          placeholder="Añade una breve descripción del video"></textarea>

                <label>Tipo de Video:</label>
                <select id="tipoVideo" name="tipoVideo" required>
                    <option value="">Seleccione una opción</option>
                    <option value="archivo">Subir archivo</option>
                    <option value="youtube">Enlace de YouTube</option>
                </select>

                <div id="campoArchivo" style="display: none;">
                    <label>Subir Video:</label>
                    <!--<input type="file" name="archivoVideo" accept="video/*"/>-->
                    <input type="file" name="archivoVideo" accept="video/mp4"/>
                </div>

                <div id="campoYoutube" style="display: none;">
                    <label>Enlace de YouTube:</label>
                    <input type="text" name="youtubeURL" placeholder="https://www.youtube.com/watch?v=..."/>
                </div>

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
                document.addEventListener('DOMContentLoaded', function () {
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
                document.addEventListener('DOMContentLoaded', function () {
                    var toastEl = document.getElementById('successToast');
                    var toast = new bootstrap.Toast(toastEl);
                    toast.show();
                });
            </script>
            <% } %>

            <a href="home.jsp" class="back-link">Volver al menú principal</a>
        </div>

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                var tipoVideoSelect = document.getElementById("tipoVideo");
                var campoArchivo = document.getElementById("campoArchivo");
                var campoYoutube = document.getElementById("campoYoutube");

                tipoVideoSelect.addEventListener("change", function () {
                    if (this.value === "archivo") {
                        campoArchivo.style.display = "block";
                        campoYoutube.style.display = "none";
                    } else if (this.value === "youtube") {
                        campoArchivo.style.display = "none";
                        campoYoutube.style.display = "block";
                    } else {
                        campoArchivo.style.display = "none";
                        campoYoutube.style.display = "none";
                    }
                });
            });
        </script>

        <!-- Bootstrap 5 JS (CDN) -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
    </body>
</html>