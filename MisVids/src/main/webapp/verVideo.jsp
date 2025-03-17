<%-- 
    Document   : verVideo
    Created on : Mar 14, 2025, 5:24:06 PM
    Author     : parallels
--%>

<%@ page session="true" import="com.isdcm.minetflix.utils.Utils"%>
<%
    com.isdcm.minetflix.model.Video video = (com.isdcm.minetflix.model.Video) request.getAttribute("video");
    if (video == null) {
        response.sendRedirect("listadoVid.jsp");
        return;
    }
    String rutaVideo = (String) request.getAttribute("rutaVideo");
%>
<html lang="es">
<head>
    <title>Ver Video - MiNetflix</title>
    <!-- Bootstrap 5 CSS (CDN) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" 
          rel="stylesheet" 
          crossorigin="anonymous">
    <link rel="stylesheet" href="css/verVideo.css">
</head>
<body class="bg-dark text-light">
    <div class="container mt-5">
        <h2 class="text-center mb-4 text-danger"><%= video.getTitulo() %></h2>
        
        <div class="d-flex justify-content-center">
            <% if ("YOUTUBE".equals(video.getTipoFuente())) { %>
                <div id="player"></div>
                <script>
                    var player;
                    var reproduccionRegistrada = false;  
                    
                    function onYouTubeIframeAPIReady() {
                        player = new YT.Player("player", {
                            height: "450",
                            width: "800",
                            videoId: '<%= Utils.extraerYouTubeId(video.getRutaVideo()) %>',
                            events: {
                                onStateChange: onPlayerStateChange
                            }
                        });
                    }
                    
                    function onPlayerStateChange(event) {
                        if (event.data === YT.PlayerState.PLAYING && !reproduccionRegistrada) {
                            reproduccionRegistrada = true;
                            
                            fetch("servletVerVideo?id=<%= video.getId() %>&accion=reproducido", {
                                method: "POST"
                            })
                        }
                    }
                </script>
                <script src="https://www.youtube.com/iframe_api"></script>

                    <!-- Cargar la API de YouTube -->
                    <script src="https://www.youtube.com/iframe_api"></script>
            <% } else { %>
                <video width="800" height="450" controls>
                    <source src="<%= rutaVideo %>" type="<%= video.getMimeType() %>">
                    Your browser does not support videos.
                </video>
            <% } %>
        </div>

        <div class="mt-3">
            <p><strong>Autor:</strong> <%= video.getAutor() %></p>
            <p><strong>Fecha:</strong> <%= video.getFecha() %></p>
            <p><strong>Descripción:</strong> <%= video.getDescripcion() %></p>
            <p><strong>Reproducciones</strong> <%= video.getReproducciones() %></p>
        </div>

        <a href="servletListadoVid" class="btn btn-danger mt-3">Volver al listado</a>


        <% if (request.getAttribute("mensajeError") != null) { %>
            <!-- Contenedor para el toast de error -->
            <div class="position-fixed top-0 end-0 p-3" style="z-index: 9999">
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
            <script>
                document.addEventListener('DOMContentLoaded', function() {
                    var toastEl = document.getElementById('errorToast');
                    var toast = new bootstrap.Toast(toastEl);
                    toast.show();
                });
            </script>
        <% } %>

        <% if (request.getAttribute("mensajeExito") != null) { %>
            <div class="position-fixed top-0 end-0 p-3" style="z-index: 9999;">
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
            <script>
                document.addEventListener('DOMContentLoaded', function() {
                    var toastEl = document.getElementById('successToast');
                    var toast = new bootstrap.Toast(toastEl);
                    toast.show();
                });
            </script>
        <% } %>

    </div>
    
    <!-- Bootstrap 5 JS (CDN) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
            crossorigin="anonymous"></script>
</body>
</html>
