<%-- 
    Document   : listadoVid
    Created on : 21 feb 2025, 14:30:20
    Author     : alumne
--%>

<%@ page session="true" %>
<%
    if (session.getAttribute("usuarioLogueado") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<html lang="es">
<head>
    <title>Listado de Videos - MiNetflix</title>
    <!-- Bootstrap 5 CSS (CDN) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" 
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet" href="css/listadoVid.css">
</head>
<body>
    <div class="container">
        <h2>Listado de Videos</h2>

        <table>
            <thead>
                <tr>
                    <th>Título</th>
                    <th>Autor</th>
                    <th>Fecha</th>
                    <th>Duración</th>
                    <th>Reproducciones</th>
                    <th>Descripción</th>
                    <th>Formato</th>
                    <th>Tamaño</th>
                    <th>Enlace</th>
                </tr>
            </thead>
            <tbody>
            <%
                java.util.List<com.isdcm.minetflix.model.Video> listaVideos = 
                    (java.util.List<com.isdcm.minetflix.model.Video>) request.getAttribute("listaVideos");

                if (listaVideos != null && !listaVideos.isEmpty()) {
                    for (com.isdcm.minetflix.model.Video vid : listaVideos) {
            %>
                <tr>
                    <td><%= vid.getTitulo() %></td>
                    <td><%= vid.getAutor() %></td>
                    <td><%= vid.getFecha() %></td>
                    <td><%= vid.getDuracion() %></td>
                    <td><%= vid.getReproducciones() %></td>
                    <td><%= vid.getDescripcion() %></td>
                    <td><%= vid.getMimeType() %></td>
                    <td><%= vid.getTamano() %></td>
                    <td><a href="servletVerVideo?id=<%= vid.getId() %>" class="link">Ver video</a></td>            <%
                    }
                } else {
            %>
                <tr>
                    <td colspan="8">No hay videos registrados.</td>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>
            
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