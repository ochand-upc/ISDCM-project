<%-- 
    Document   : verVideo
    Created on : 28 feb 2025, 16:50:35
    Author     : alumne
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
        <meta charset="UTF-8">
        <title>Ver Video - MiNetflix</title>
        <!-- Bootstrap 5 CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" 
              rel="stylesheet"
              integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
              crossorigin="anonymous">
        <!-- Bootstrap Icons -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css">
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
                                onStateChange: async function (e) {
                                    if (e.data === YT.PlayerState.PLAYING && !reproduccionRegistrada) {
                                        reproduccionRegistrada = true;
                                        try {
                                            const res = await fetch(
                                                    'servletRest?action=views&id=<%= video.getId() %>',
                                                    {method: "PUT"});
                                            if (!res.ok) {
                                                const err = await res.json().catch(() => ({error: res.statusText}));
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
                    <video id="playerLocal" controls preload="none" crossorigin="anonymous">
                        <source src="servletRest?action=stream&id=<%= video.getId() %>" type="<%= video.getMimeType() %>">
                        Tu navegador no soporta videos.
                    </video>
                </div>
                <script>
                    document.addEventListener("DOMContentLoaded", function () {
                        var videoEl = document.getElementById("playerLocal");
                        var reproduccionRegistrada = false;
                        
                        // Manejar eventos de reproducción
                        videoEl.addEventListener("play", async function () {
                            if (!reproduccionRegistrada) {
                                reproduccionRegistrada = true;
                                try {
                                    const res = await fetch(
                                            'servletRest?action=views&id=<%= video.getId() %>',
                                            {method: "PUT"});
                                    if (!res.ok) {
                                        const err = await res.json().catch(() => ({error: res.statusText}));
                                        showToast(err.error || 'Error desconocido al registrar vista');
                                    }
                                } catch (e) {
                                    showToast('No se pudo conectar al servidor');
                                }
                            }
                        });
                        
                        // Variable para controlar si ya se mostró error
                        var errorMostrado = false;
                        
                        // Manejar errores del video
                        videoEl.addEventListener("error", async function(e) {
                            if (errorMostrado) return; // Evitar múltiples errores
                            errorMostrado = true;
                            
                            console.error("Error en el video player:", e);
                            
                            // Obtener detalles del error desde el servidor
                            try {
                                console.log("Obteniendo detalles del error del servidor...");
                                const response = await fetch('servletRest?action=stream&id=<%= video.getId() %>', {
                                    method: 'GET',
                                    headers: {
                                        'Range': 'bytes=0-1' // Petición mínima para obtener error
                                    }
                                });
                                
                                if (!response.ok) {
                                    // El servidor nos dio un error detallado
                                    const errorData = await response.json().catch(() => null);
                                    
                                    if (errorData && errorData.error) {
                                        // Mostrar error específico del servidor
                                        const fullMessage = errorData.error + ": " + errorData.details;
                                        showToast(fullMessage, true);
                                        mostrarErrorEnVideo(errorData.error, errorData.details);
                                        console.error("Error del servidor:", errorData);
                                        return;
                                    }
                                }
                            } catch (fetchError) {
                                console.error("Error obteniendo detalles del servidor:", fetchError);
                            }
                            
                            // Fallback: usar el error genérico del player si no pudimos obtener detalles
                            const error = videoEl.error;
                            let errorMessage = "Error de reproducción";
                            let errorDetails = "";
                            
                            if (error) {
                                switch(error.code) {
                                    case error.MEDIA_ERR_ABORTED:
                                        errorMessage = "Reproducción cancelada";
                                        errorDetails = "La carga del video fue interrumpida.";
                                        break;
                                    case error.MEDIA_ERR_NETWORK:
                                        errorMessage = "Error de conexión";
                                        errorDetails = "No se pudo descargar el video. El archivo puede no existir en el servidor.";
                                        break;
                                    case error.MEDIA_ERR_DECODE:
                                        errorMessage = "Error de decodificación";
                                        errorDetails = "El archivo de video está corrupto o no se puede descifrar correctamente.";
                                        break;
                                    case error.MEDIA_ERR_SRC_NOT_SUPPORTED:
                                        errorMessage = "Video no disponible";
                                        errorDetails = "El formato no es compatible o el archivo está dañado.";
                                        break;
                                    default:
                                        errorDetails = "Se produjo un error desconocido durante la reproducción.";
                                }
                            } else {
                                errorDetails = "No se pudo cargar el video. Es posible que el archivo haya sido eliminado del servidor.";
                            }
                            
                            // Mostrar error al usuario
                            showToast(errorMessage + ": " + errorDetails, true);
                            mostrarErrorEnVideo(errorMessage, errorDetails);
                        });
                        
                        // Detectar si el video no se puede cargar (error 404/500 del servidor)
                        videoEl.addEventListener("loadstart", function() {
                            console.log("Iniciando carga del video ID <%= video.getId() %>...");
                            
                            // Timeout más agresivo para detectar si el video no carga
                            const loadTimeout = setTimeout(function() {
                                if (videoEl.readyState === videoEl.HAVE_NOTHING && !errorMostrado) {
                                    console.log("Timeout: El video no ha cargado después de 8 segundos");
                                    errorMostrado = true;
                                    mostrarErrorEnVideo("Video no disponible", 
                                        "El video no se pudo cargar en un tiempo razonable. " +
                                        "Es posible que el archivo no exista en el servidor.");
                                    showToast('Timeout: El video no se pudo cargar. Es posible que el archivo haya sido eliminado.', true);
                                }
                            }, 8000); // 8 segundos
                            
                            // Limpiar timeout si el video empieza a cargar
                            const clearTimeoutOnProgress = function() {
                                if (videoEl.readyState > videoEl.HAVE_NOTHING) {
                                    clearTimeout(loadTimeout);
                                    videoEl.removeEventListener('progress', clearTimeoutOnProgress);
                                    videoEl.removeEventListener('loadedmetadata', clearTimeoutOnProgress);
                                }
                            };
                            
                            videoEl.addEventListener('progress', clearTimeoutOnProgress);
                            videoEl.addEventListener('loadedmetadata', clearTimeoutOnProgress);
                        });
                        
                        // Verificación preventiva del estado del video al cargar la página
                        async function verificarDisponibilidadVideo() {
                            try {
                                console.log("Verificando disponibilidad del video <%= video.getId() %>...");
                                const response = await fetch('servletRest?action=stream&id=<%= video.getId() %>', {
                                    method: 'HEAD' // Solo headers, no descargar contenido
                                });
                                
                                if (!response.ok) {
                                    console.warn("Video no disponible - Status:", response.status);
                                    
                                    // Intentar obtener detalles del error
                                    const errorResponse = await fetch('servletRest?action=stream&id=<%= video.getId() %>', {
                                        method: 'GET',
                                        headers: { 'Range': 'bytes=0-1' }
                                    });
                                    
                                    if (!errorResponse.ok) {
                                        const errorData = await errorResponse.json().catch(() => null);
                                        if (errorData && errorData.error && !errorMostrado) {
                                            errorMostrado = true;
                                            const fullMessage = errorData.error + ": " + errorData.details;
                                            showToast(fullMessage, true);
                                            mostrarErrorEnVideo(errorData.error, errorData.details);
                                            return;
                                        }
                                    }
                                    
                                    // Error genérico si no pudimos obtener detalles
                                    if (!errorMostrado) {
                                        errorMostrado = true;
                                        mostrarErrorEnVideo("Video no disponible", 
                                            "El archivo de video no se encuentra disponible en el servidor.");
                                        showToast('El video no está disponible para reproducción.', true);
                                    }
                                } else {
                                    console.log("Video <%= video.getId() %> disponible para streaming");
                                }
                            } catch (error) {
                                console.warn("No se pudo verificar la disponibilidad del video:", error);
                                // No mostrar error preventivo si falla la verificación, 
                                // dejar que el usuario intente reproducir normalmente
                            }
                        }
                        
                        // Ejecutar verificación preventiva después de 1 segundo
                        setTimeout(verificarDisponibilidadVideo, 1000);
                        
                        // Eventos de estado del video (solo para debugging)
                        videoEl.addEventListener("canplay", function() {
                            console.log("Video <%= video.getId() %> listo para reproducir - Duración: " + videoEl.duration + "s");
                        });
                        
                        videoEl.addEventListener("loadeddata", function() {
                            console.log("Video <%= video.getId() %> datos cargados - Resolución: " + videoEl.videoWidth + "x" + videoEl.videoHeight);
                        });
                        
                        videoEl.addEventListener("canplaythrough", function() {
                            console.log("Video <%= video.getId() %> completamente cargado y listo");
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

            <div class="metadata-controls">
                <a href="servletRest?action=exportMetadataEnc&id=${video.id}"
                   download="video${video.id}.metadata.xml.enc"
                   class="btn btn-danger download-btn">
                    Descargar metadatos cifrados
                </a>

                <form action="servletRest" method="post" enctype="multipart/form-data"
                      class="upload-form" target="_blank">
                    <input type="hidden" name="action" value="importMetadataEnc"/>
                    <input type="hidden" name="id"     value="${video.id}"/>

                    <input type="file" name="file" accept=".xml,.enc"
                           class="file-input" required/>

                    <button type="submit" class="btn btn-danger decrypt-btn">
                        Ver metadatos desencriptados
                    </button>
                </form>
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
            document.addEventListener("DOMContentLoaded", function () {
                document.querySelectorAll('.date').forEach(span => {
                    const iso = span.dataset.iso;
                    span.textContent = formatDate(iso);
                });

                // Mostrar errores desde servlet o sesión
            <% 
                String mensajeError = (String) request.getAttribute("mensajeError");
                if (mensajeError == null) {
                    mensajeError = (String) session.getAttribute("mensajeError");
                    if (mensajeError != null) {
                        session.removeAttribute("mensajeError"); // Limpiar después de mostrar
                    }
                }
                if (mensajeError != null) { 
            %>
                showToast("<%= mensajeError.replace("\"", "\\\"" ).replace("\n", "\\n") %>");
                
                // Si es un error relacionado con video no encontrado, mostrar interfaz de error
                <% if (mensajeError.toLowerCase().contains("no encontrado") || 
                       mensajeError.toLowerCase().contains("not found") ||
                       mensajeError.toLowerCase().contains("eliminado")) { %>
                    setTimeout(function() {
                        mostrarErrorEnVideo("Video No Disponible", 
                            "El archivo de video no se encuentra en el servidor. " +
                            "Es posible que haya sido eliminado o movido.");
                    }, 1000);
                <% } %>
            <% } %>
            });

            function formatDate(iso) {
                const d = new Date(iso.replace(' ', 'T'));
                const opciones = {day: 'numeric', month: 'long', year: 'numeric'};
                return d.toLocaleDateString('es-ES', opciones);
            }

            // mostrar toast
            function showToast(textContent, error = true) {
                const toastEl = document.getElementById("customToast");
                const toastBody = toastEl.querySelector(".toast-body");
                const toast = new bootstrap.Toast(toastEl);

                if (error) {
                    toastEl.classList.remove("text-bg-success");
                    toastEl.classList.add("text-bg-danger");
                } else {
                    toastEl.classList.add("text-bg-success");
                    toastEl.classList.remove("text-bg-danger");
                }
                toastBody.textContent = textContent;
                // Mostrar el toast
                toast.show();
            }
            
            // Mostrar interfaz de error en el contenedor del video
            function mostrarErrorEnVideo(titulo, detalle) {
                const videoEl = document.getElementById("playerLocal");
                if (videoEl) {
                    // Ocultar el video player
                    videoEl.style.display = 'none';
                    
                    // Encontrar el contenedor del video
                    const videoContainer = videoEl.closest('.ratio');
                    if (videoContainer) {
                        videoContainer.innerHTML = 
                            '<div class="d-flex align-items-center justify-content-center h-100 bg-light text-center p-4 border rounded">' +
                            '<div>' +
                            '<i class="bi bi-exclamation-triangle text-danger" style="font-size: 3rem;"></i>' +
                            '<h5 class="mt-3 text-danger">' + titulo + '</h5>' +
                            '<p class="text-muted mb-3">' + detalle + '</p>' +
                            '<div class="d-flex gap-2 justify-content-center">' +
                            '<button class="btn btn-outline-primary btn-sm" onclick="location.reload()">'+
                            '<i class="bi bi-arrow-clockwise me-1"></i>Reintentar</button>' +
                            '<a href="listadoVid.jsp" class="btn btn-outline-secondary btn-sm">' +
                            '<i class="bi bi-arrow-left me-1"></i>Volver al listado</a>' +
                            '</div>' +
                            '</div>' +
                            '</div>';
                    }
                }
            }


        </script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
    </body>
</html>
