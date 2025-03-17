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
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" 
          rel="stylesheet" crossorigin="anonymous">
    <link rel="stylesheet" href="css/verVideo.css">
</head>
<body>
    <div class="container">
        <h2><%= video.getTitulo() %></h2>

        <div class="video-container">
            <% if ("YOUTUBE".equals(video.getTipoFuente())) { %>
                <div class="ratio ratio-16x9">
                    <div id="player"></div>
                </div>
                <script>
                    var player;
                    var reproduccionRegistrada = false;  
                    
                    function onYouTubeIframeAPIReady() {
                        player = new YT.Player("player", {
                            height: "450",
                            width: "800",
                            videoId: '<%= Utils.extraerYouTubeId(rutaVideo) %>',
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
                            });
                        }
                    }
                </script>
                <script src="https://www.youtube.com/iframe_api"></script>
            <% } else { %>
                <div class="ratio ratio-16x9">
                    <video controls>
                        <source src="<%= rutaVideo %>" type="<%= video.getMimeType() %>">
                        Tu navegador no soporta videos.
                    </video>
                </div>
            <% } %>
        </div>

        <div class="video-info">
            <p><strong>Autor:</strong> <%= video.getAutor() %></p>
            <p><strong>Fecha:</strong> <%= video.getFecha() %></p>
            <p><strong>Descripción:</strong> <%= video.getDescripcion() %></p>
            <p><strong>Reproducciones:</strong> <%= video.getReproducciones() %></p>
        </div>

        <a href="servletListadoVid" class="btn btn-danger">Volver al listado</a>

        <!-- Toasts de mensajes de error y éxito -->
        <% if (request.getAttribute("mensajeError") != null || request.getAttribute("mensajeExito") != null) { %>
            <div class="position-fixed top-0 end-0 p-3" style="z-index: 9999;">
                <div id="errorToast" class="toast align-items-center text-bg-<%= request.getAttribute("mensajeError") != null ? "danger" : "success" %> border-0"
                     role="alert" aria-live="assertive" aria-atomic="true" data-bs-autohide="false">
                    <div class="d-flex">
                        <div class="toast-body">
                            <%= request.getAttribute("mensajeError") != null ? request.getAttribute("mensajeError") : request.getAttribute("mensajeExito") %>
                        </div>
                        <button type="button" class="btn-close btn-close-white me-2 m-auto"
                                data-bs-dismiss="toast" aria-label="Close"></button>
                    </div>
                </div>
            </div>
            <script>
                document.addEventListener('DOMContentLoaded', function() {
                    var toast = new bootstrap.Toast(document.getElementById('errorToast'));
                    toast.show();
                });
            </script>
        <% } %>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
</body>
</html>