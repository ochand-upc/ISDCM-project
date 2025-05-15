<%-- 
    Document   : verVideo
    Created on : 28 feb 2025, 16:50:35
    Author     : alumne
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
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" 
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet" href="css/verVideo.css">
</head>
<body>
    <div class="container">
        <h2><%= video.getTitulo() %></h2>

        <div class="video-container">
        <% if ("YOUTUBE".equals(video.getTipoFuente())) { %>
            <div class="ratio ratio-16x9">
                <div id="playerYT"></div>
            </div>
            <script>
                var reproduccionRegistrada = false;
                function onYouTubeIframeAPIReady() {
                    new YT.Player("playerYT", {
                        height: "450",
                        width: "800",
                        videoId: '<%= Utils.extraerYouTubeId(rutaVideo) %>',
                        events: {
                            onStateChange: async function(e) {
                                if (e.data === YT.PlayerState.PLAYING && !reproduccionRegistrada) {
                                    reproduccionRegistrada = true;
                                    try {
                                        const res = await fetch(
                                          'servletRest?action=views&id=<%= video.getId() %>', 
                                          { method: "PUT" });
                                        if (!res.ok) {
                                          const err = await res.json().catch(()=>({ error: res.statusText }));
                                          showToast(err.error || 'Error desconocido al registrar vista');
                                        }
                                    } catch (e) {
                                      showToast('No se pudo conectar al servidor');
                                    }
                                }
                            }
                        }
                    });
                }
            </script>
            <script src="https://www.youtube.com/iframe_api"></script>

            <% } else { %>
                <div class="ratio ratio-16x9">
                    <video id="playerLocal" controls preload="metadata">
                        <source src="servletRest?action=stream&id=<%= video.getId() %>" type="<%= video.getMimeType() %>">
                        Tu navegador no soporta videos.
                    </video>
                </div>
                <script>
                  document.addEventListener("DOMContentLoaded", function() {
                    var videoEl = document.getElementById("playerLocal");
                    var reproduccionRegistrada = false;
                    videoEl.addEventListener("play", async function() {
                      if (!reproduccionRegistrada) {
                        reproduccionRegistrada = true;
                        try {
                            const res = await fetch(
                              'servletRest?action=views&id=<%= video.getId() %>', 
                              { method: "PUT" });
                            if (!res.ok) {
                              const err = await res.json().catch(()=>({ error: res.statusText }));
                              showToast(err.error || 'Error desconocido al registrar vista');
                            }
                        } catch (e) {
                          showToast('No se pudo conectar al servidor');
                        }
                      }
                    });
                  });
                </script>
            <% } %>
        </div>

        <div class="video-info mt-3">
            <p><strong>Autor:</strong> <%= video.getAutor() %></p>
            <p><strong>Fecha:</strong>
              <span class="date" data-iso="<%= video.getFecha() %>">
              </span>
            </p>            
            <p><strong>Descripción:</strong> <%= video.getDescripcion() %></p>
            <p><strong>Vistas:</strong> <%= video.getReproducciones() %></p>
        </div>

        <a href="listadoVid.jsp" class="btn btn-danger mt-2">Volver al listado</a>
        
        <div class="position-fixed top-0 end-0 p-3" style="z-index: 9999;">
            <div id="customToast" class="toast align-items-center text-bg-danger border-0"
               role="alert" aria-live="assertive" aria-atomic="true"
               data-bs-autohide="false">
                  <div class="d-flex">
                      <div class="toast-body">
                          <!-- Mensaje dinámico -->
                      </div>
                      <button type="button" class="btn-close btn-close-white me-2 m-auto"
                              data-bs-dismiss="toast" aria-label="Close"></button>
                  </div>
            </div>            
        </div>
    </div>
    
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            document.querySelectorAll('.date').forEach(span => {
                const iso = span.dataset.iso;
                span.textContent = formatDate(iso);
            });
            
            //Mostrar errores desde servlet
            <% if (request.getAttribute("mensajeError") != null) { %>
                showToast("<%= request.getAttribute("mensajeError") %>");
            <% } %>
        });
        
        function formatDate(iso) {
            const d = new Date(iso.replace(' ', 'T'));
            const opciones = { day: 'numeric', month: 'long', year: 'numeric' };
            return d.toLocaleDateString('es-ES', opciones);
        }
        
        // mostrar toast
        function showToast(textContent, error=true){
            const toastEl = document.getElementById("customToast");
            const toastBody = toastEl.querySelector(".toast-body");
            const toast = new bootstrap.Toast(toastEl);

            if(error){
                toastEl.classList.remove("text-bg-success");
                toastEl.classList.add("text-bg-danger");
            }else{
               toastEl.classList.add("text-bg-success");
               toastEl.classList.remove("text-bg-danger"); 
            }
            toastBody.textContent = textContent;
            // Mostrar el toast
            toast.show();
        }
    
    
    </script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
            crossorigin="anonymous"></script>
</body>
</html>