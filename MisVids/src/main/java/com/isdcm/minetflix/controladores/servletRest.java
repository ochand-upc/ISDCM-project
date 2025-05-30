package com.isdcm.minetflix.controladores;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isdcm.minetflix.model.PaginatedResponse;
import com.isdcm.minetflix.model.Video;
import com.isdcm.minetflix.model.VideoFilter;
import com.isdcm.minetflix.utils.AppConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "servletRest", urlPatterns = {"/servletRest"})
@MultipartConfig
public class servletRest extends HttpServlet {

    private static final String API_BASE = AppConfig.get("web-service.url") + "/videos";

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        String jwt = (session == null) ? null : (String) session.getAttribute("jwt");
        if (session == null || jwt == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String action = req.getParameter("action");
        if ("stream".equals(action)) {
            // Para HEAD requests de streaming, solo verificar disponibilidad
            verificarDisponibilidadVideo(req, resp, jwt);
        } else {
            resp.sendError(400, "Acción HEAD no soportada");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        String jwt = (session == null) ? null : (String) session.getAttribute("jwt");
        if (session == null || jwt == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String id = req.getParameter("id");
        String action = req.getParameter("action");

        switch (action) {
            case "stream":
                proxyStream(req, resp, jwt);
                break;
            case "view":
                pageView(req, resp, jwt);
                break;
            case "exportMetadataEnc":
                resp.setHeader("Content-Disposition",
                        "attachment; filename=\"video" + id + ".metadata.xml.enc\"");
                proxyTo(API_BASE + "/" + id + "/metadata/encrypted", resp, jwt);
                break;

            default:
                resp.sendError(400, "Acción GET desconocida");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        String jwt = (session == null) ? null : (String) session.getAttribute("jwt");
        if (session == null || jwt == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        // 1) LEER action (getParameter o, en multipart, como Part)
        String action = req.getParameter("action");
        if (action == null) {
            Part actionPart = req.getPart("action");
            if (actionPart != null) {
                try (BufferedReader r = new BufferedReader(
                        new InputStreamReader(actionPart.getInputStream(), StandardCharsets.UTF_8))) {
                    action = r.lines().collect(Collectors.joining());
                }
            }
        }
        if (action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro 'action'");
            return;
        }

        // 2) Busqueda
        if ("search".equals(action)) {
            buscarVideos(req, resp, jwt);
            return;
        }

        // 3) Recuperar id del documento
        String id = req.getParameter("id");
        if (id == null) {
            Part idPart = req.getPart("id");
            if (idPart != null) {
                try (BufferedReader r = new BufferedReader(
                        new InputStreamReader(idPart.getInputStream(), StandardCharsets.UTF_8))) {
                    id = r.lines().collect(Collectors.joining());
                }
            }
        }
        if (id == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro 'id'");
            return;
        }

        // 4) Subir .enc y proxy a /metadata/decrypt
        if ("importMetadataEnc".equals(action)) {
            Part filePart = req.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Fichero cifrado no proporcionado");
                return;
            }

            String targetUrl = API_BASE + "/" + id + "/metadata/decrypt";
            HttpURLConnection conn = (HttpURLConnection) new URL(targetUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + jwt);
            conn.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");
            conn.setDoOutput(true);

            // Enviar body
            try (InputStream in = filePart.getInputStream(); OutputStream out = conn.getOutputStream()) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
            }
            conn.connect();

            // Retornar status code y headers
            int status = conn.getResponseCode();
            resp.setStatus(status);

            if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
                resp.sendRedirect("login.jsp?error=token");
                return;
            }

            conn.getHeaderFields().forEach((k, vList) -> {
                if (k != null) {
                    vList.forEach(v -> resp.addHeader(k, v));
                }
            });

            // Propagar body de respuesta
            try (InputStream in = conn.getInputStream(); OutputStream out = resp.getOutputStream()) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
            } finally {
                conn.disconnect();
            }
            return;
        }

        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción POST desconocida: " + action);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        String jwt = (session == null) ? null : (String) session.getAttribute("jwt");
        if (session == null || jwt == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String action = req.getParameter("action");
        if ("views".equals(action)) {
            actualizarVistas(req, resp, jwt);
        } else {
            resp.sendError(404);
        }
    }

    private void pageView(HttpServletRequest req, HttpServletResponse resp, String jwt)
            throws ServletException, IOException {
        // 1) parsear id
        String sId = req.getParameter("id");
        if (sId == null || sId.isEmpty()) {
            resp.sendRedirect("listadoVid.jsp");
            return;
        }
        int id = Integer.parseInt(sId);

        // 2) llamar al endpoint GET /api/videos/{id}
        URL url = new URL(API_BASE + "/" + id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + jwt);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() == 200) {
            Video video = mapper.readValue(conn.getInputStream(), Video.class);

            // 3) armar rutaVideo según tipo
            String rutaVideo;
            if ("LOCAL".equals(video.getTipoFuente())) {
                // apuntamos al stream proxy de este mismo servlet
                rutaVideo = "servletRest?action=stream&id=" + id;
            } else {
                rutaVideo = video.getRutaVideo();
            }

            // 4) forward al JSP con los atributos que necesita
            req.setAttribute("video", video);
            req.setAttribute("rutaVideo", rutaVideo);
            req.getRequestDispatcher("verVideo.jsp").forward(req, resp);

        } else {
            // 5) manejar error al obtener metadata del video
            int responseCode = conn.getResponseCode();
            String errorMessage;
            
            if (responseCode == 404) {
                errorMessage = "Video no encontrado: El video con ID " + id + " no existe en el sistema.";
            } else if (responseCode == 500) {
                errorMessage = "Error del servidor: No se pudo obtener la información del video.";
            } else {
                errorMessage = "Error al cargar el video: Código de error " + responseCode + ".";
            }
            
            req.getSession().setAttribute("mensajeError", errorMessage);
            resp.sendRedirect("listadoVid.jsp");
        }
    }

    private void proxyStream(HttpServletRequest req, HttpServletResponse resp, String jwt)
            throws ServletException, IOException {
        
        String id = req.getParameter("id");
        URL url = new URL(API_BASE + "/" + id + "/stream");
        HttpURLConnection conn = null;
        
        try {
            conn = (HttpURLConnection) url.openConnection();
            
            // Propaga rango si existe
            String range = req.getHeader("Range");
            if (range != null) {
                conn.setRequestProperty("Range", range);
            }
            
            conn.setRequestProperty("Authorization", "Bearer " + jwt);
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            resp.setStatus(status);

            if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
                resp.sendRedirect("login.jsp?error=token");
                return;
            }
            
            // Manejar errores específicos del backend
            if (status >= 400) {
                // Leer mensaje de error del backend
                String errorContent = "";
                String errorType = "";
                try (InputStream errorStream = conn.getErrorStream()) {
                    if (errorStream != null) {
                        errorContent = new BufferedReader(new InputStreamReader(errorStream))
                                .lines().collect(Collectors.joining("\n"));
                        
                        // Intentar extraer tipo de error del JSON
                        if (errorContent.contains("\"type\":")) {
                            int typeStart = errorContent.indexOf("\"type\":") + 8;
                            int typeEnd = errorContent.indexOf("\"", typeStart + 1);
                            if (typeEnd > typeStart) {
                                errorType = errorContent.substring(typeStart + 1, typeEnd);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error leyendo mensaje de error del backend: " + e.getMessage());
                }
                
                // Log del error para debugging
                System.err.println("Error en streaming de video " + id + " - Status: " + status + " - Error: " + errorContent);
                
                // NO hacer redirect para requests de streaming, devolver error directo
                // El JavaScript del frontend manejará estos errores
                resp.setStatus(status);
                resp.setContentType("application/json");
                
                // Crear respuesta de error estructurada para el frontend
                String errorResponse;
                if (status == 416) { // Range Not Satisfiable
                    errorResponse = "{\"error\":\"Rango de bytes inválido\",\"details\":\"El archivo puede estar corrupto\",\"code\":416,\"videoId\":" + id + "}";
                } else if (status == 422) { // Unprocessable Entity
                    if ("VIDEO_NOT_ENCRYPTED".equals(errorType)) {
                        errorResponse = "{\"error\":\"Video no cifrado\",\"details\":\"El video no está cifrado correctamente\",\"code\":422,\"videoId\":" + id + "}";
                    } else if ("VIDEO_CORRUPTED".equals(errorType)) {
                        errorResponse = "{\"error\":\"Video corrupto\",\"details\":\"El archivo está dañado\",\"code\":422,\"videoId\":" + id + "}";
                    } else if ("DECRYPTION_FAILED".equals(errorType)) {
                        errorResponse = "{\"error\":\"Error de descifrado\",\"details\":\"No se pudo descifrar el video\",\"code\":422,\"videoId\":" + id + "}";
                    } else {
                        errorResponse = "{\"error\":\"Video no procesable\",\"details\":\"El archivo no se puede procesar\",\"code\":422,\"videoId\":" + id + "}";
                    }
                } else if (status == 404) {
                    if ("VIDEO_NOT_FOUND".equals(errorType)) {
                        errorResponse = "{\"error\":\"Video no encontrado\",\"details\":\"El archivo ha sido eliminado del servidor\",\"code\":404,\"videoId\":" + id + "}";
                    } else {
                        errorResponse = "{\"error\":\"Video no disponible\",\"details\":\"El archivo no se encuentra\",\"code\":404,\"videoId\":" + id + "}";
                    }
                } else if (status == 500) {
                    errorResponse = "{\"error\":\"Error del servidor\",\"details\":\"No se puede procesar el video\",\"code\":500,\"videoId\":" + id + "}";
                } else {
                    errorResponse = "{\"error\":\"Error de reproducción\",\"details\":\"No se puede cargar el video\",\"code\":" + status + ",\"videoId\":" + id + "}";
                }
                
                resp.getWriter().write(errorResponse);
                return;
            }

            // Copiar headers relevantes para streaming
            for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
                String key = header.getKey();
                if (key == null) {
                    continue;
                }
                if (key.equalsIgnoreCase("Content-Type")
                        || key.equalsIgnoreCase("Content-Length")
                        || key.equalsIgnoreCase("Accept-Ranges")
                        || key.equalsIgnoreCase("Content-Range")
                        || key.equalsIgnoreCase("Cache-Control")
                        || key.equalsIgnoreCase("Pragma")
                        || key.equalsIgnoreCase("Expires")) {
                    for (String v : header.getValue()) {
                        resp.addHeader(key, v);
                    }
                }
            }
            
            // Log del status y headers importantes
            System.out.println("Proxy streaming video " + id + " - Status: " + status);
            if (range != null) {
                String contentRange = conn.getHeaderField("Content-Range");
                System.out.println("Content-Range: " + contentRange);
            }

            // Stream de bytes con manejo robusto de desconexiones
            try (InputStream in = conn.getInputStream()) {
                OutputStream out = resp.getOutputStream();
                byte[] buf = new byte[16384]; // Buffer más grande
                int len;
                long totalSent = 0;
                
                while ((len = in.read(buf)) != -1) {
                    try {
                        out.write(buf, 0, len);
                        totalSent += len;
                        
                        // Flush menos frecuente para no interferir con Range requests
                        if (totalSent % (512 * 1024) == 0) { // Cada 512KB
                            out.flush();
                        }
                        
                    } catch (IOException e) {
                        // Solo loggear desconexiones si se ha transferido una cantidad significativa
                        if (totalSent > 64 * 1024) { // Solo si se han transferido más de 64KB
                            System.out.println("Cliente desconectado durante proxy streaming de video " + id + 
                                             " (transferidos " + totalSent + " bytes)");
                        }
                        break; // Salir limpiamente
                    }
                }
                
                // Flush final para asegurar entrega
                try {
                    out.flush();
                    if (totalSent > 0) {
                        System.out.println("Proxy streaming de video " + id + " completado: " + totalSent + " bytes transferidos");
                    }
                } catch (IOException e) {
                    // Ignorar errores de flush final
                }
                
            } catch (IOException e) {
                // Error en la conexión con el web-service o problema de red
                String errorMsg = "Error durante proxy streaming de video " + id + ": " + e.getMessage();
                
                // Solo loggear errores severos, no desconexiones menores
                if (!isMinorDisconnectionError(e)) {
                    System.err.println(errorMsg);
                    throw new ServletException(errorMsg, e);
                }
            }
            
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void buscarVideos(HttpServletRequest req, HttpServletResponse resp, String jwt)
            throws ServletException, IOException {

        // 1) Leer el JSON del body y deserializarlo a VideoFilter
        VideoFilter filter = mapper.readValue(req.getReader(), VideoFilter.class);

        // 2) Llamar al endpoint real
        URL url = new URL(API_BASE + "/search");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + jwt);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        mapper.writeValue(conn.getOutputStream(), filter);

        // 3) Leer respuesta y mapear a PaginatedResponse<Video>
        int status = conn.getResponseCode();
        InputStream in = (status == 200)
                ? conn.getInputStream()
                : conn.getErrorStream();

        // 4) Configurar el response del servlet
        resp.setStatus(status);
        resp.setContentType("application/json; charset=UTF-8");

        if (status == HttpURLConnection.HTTP_OK) {
            // devuelve el objeto completo como JSON
            PaginatedResponse<Video> page = mapper.readValue(
                    in,
                    new TypeReference<PaginatedResponse<Video>>() {
            }
            );
            mapper.writeValue(resp.getWriter(), page);

        } else if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
            resp.sendRedirect("login.jsp?error=token");
            return;
        } else {
            // opcional: propaga el error tal cual vino
            String errorJson = new BufferedReader(new InputStreamReader(in))
                    .lines().collect(Collectors.joining("\n"));
            resp.getWriter().print(errorJson);
        }
    }

    private void proxyTo(String targetUrl, HttpServletResponse resp, String jwt) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(targetUrl).openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + jwt);
        conn.setRequestMethod("GET");
        conn.connect();
        resp.setStatus(conn.getResponseCode());
        if (conn.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            resp.sendRedirect("login.jsp?error=token");
            return;
        }

        conn.getHeaderFields().forEach((k, vList) -> {
            if (k != null) {
                for (String v : vList) {
                    resp.addHeader(k, v);
                }
            }
        });
        try (InputStream is = conn.getInputStream(); OutputStream os = resp.getOutputStream()) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = is.read(buf)) != -1) {
                os.write(buf, 0, r);
            }
        } finally {
            conn.disconnect();
        }
    }

    private void verificarDisponibilidadVideo(HttpServletRequest req, HttpServletResponse resp, String jwt)
            throws ServletException, IOException {
        
        String id = req.getParameter("id");
        URL url = new URL(API_BASE + "/" + id + "/stream");
        HttpURLConnection conn = null;
        
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + jwt);
            conn.setRequestMethod("HEAD"); // Solo verificar headers

            int status = conn.getResponseCode();
            resp.setStatus(status);

            if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
                return;
            }
            
            // Si hay error, devolver información del error como JSON
            if (status >= 400) {
                resp.setContentType("application/json");
                
                String errorResponse;
                if (status == 404) {
                    errorResponse = "{\"error\":\"Video no encontrado\",\"details\":\"El archivo ha sido eliminado del servidor\",\"code\":404,\"videoId\":" + id + "}";
                } else if (status == 422) {
                    errorResponse = "{\"error\":\"Video no procesable\",\"details\":\"El archivo no se puede procesar\",\"code\":422,\"videoId\":" + id + "}";
                } else if (status == 500) {
                    errorResponse = "{\"error\":\"Error del servidor\",\"details\":\"No se puede acceder al video\",\"code\":500,\"videoId\":" + id + "}";
                } else {
                    errorResponse = "{\"error\":\"Video no disponible\",\"details\":\"No se puede cargar el video\",\"code\":" + status + ",\"videoId\":" + id + "}";
                }
                
                resp.getWriter().write(errorResponse);
                return;
            }
            
            // Si está OK, copiar headers relevantes
            String contentType = conn.getHeaderField("Content-Type");
            String contentLength = conn.getHeaderField("Content-Length");
            String acceptRanges = conn.getHeaderField("Accept-Ranges");
            
            if (contentType != null) resp.setHeader("Content-Type", contentType);
            if (contentLength != null) resp.setHeader("Content-Length", contentLength);
            if (acceptRanges != null) resp.setHeader("Accept-Ranges", acceptRanges);
            
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    /**
     * Método helper para identificar errores menores de desconexión
     */
    private boolean isMinorDisconnectionError(IOException e) {
        String message = e.getMessage();
        if (message == null) return false;
        
        return message.contains("Connection is closed") ||
               message.contains("Broken pipe") ||
               message.contains("Connection reset by peer") ||
               message.contains("ClientAbortException");
    }
    
    /**
     * Método helper para identificar errores de desconexión del cliente
     */
    private boolean isClientDisconnectionError(IOException e) {
        String message = e.getMessage();
        if (message == null) return false;
        
        return message.contains("Connection is closed") ||
               message.contains("Broken pipe") ||
               message.contains("Connection reset by peer") ||
               message.contains("ClientAbortException") ||
               message.toLowerCase().contains("connection");
    }
    
    private void actualizarVistas(HttpServletRequest req, HttpServletResponse resp, String jwt)
            throws IOException {
        String id = req.getParameter("id");
        URL url = new URL(API_BASE + "/" + id + "/views");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + jwt);
        conn.setRequestMethod("PUT");
        int status = conn.getResponseCode();
        resp.setStatus(status);

        if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
            resp.sendRedirect("login.jsp?error=token");
            return;
        }
    }
}
