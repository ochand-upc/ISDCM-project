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
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "servletRest", urlPatterns = {"/servletRest"})
public class servletRest extends HttpServlet {

    private static final String API_BASE = AppConfig.get("web-service.url") + "/videos";

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession sesion = req.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        
        String action = req.getParameter("action");
        switch (action) {
            case "stream":
                proxyStream(req, resp);
                break;
            case "view":
                pageView(req, resp);
                break;
            default:
                resp.sendError(400, "Acción GET desconocida");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession sesion = req.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        
        String action = req.getParameter("action");
        if ("search".equals(action)) {
            buscarVideos(req, resp);
        } else {
            resp.sendError(404);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession sesion = req.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        
        String action = req.getParameter("action");
        if ("views".equals(action)) {
            actualizarVistas(req, resp);
        } else {
            resp.sendError(404);
        }
    }

    private void pageView(HttpServletRequest req, HttpServletResponse resp)
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
            // 5) manejar error
            req.getSession().setAttribute("mensajeError", "No se pudo cargar el video (código "+conn.getResponseCode()+")");
            resp.sendRedirect("listadoVid.jsp");
        }
    }

    private void proxyStream(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        URL url = new URL(API_BASE + "/" + id + "/stream");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Propaga rango si existe
        String range = req.getHeader("Range");
        if (range != null) {
            conn.setRequestProperty("Range", range);
        }
        conn.setRequestMethod("GET");

        int status = conn.getResponseCode();
        resp.setStatus(status);

        // Copiar solo los headers relevantes
        for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
            String key = header.getKey();
            if (key == null) {
                continue;
            }
            if (key.equalsIgnoreCase("Content-Type")
                    || key.equalsIgnoreCase("Content-Length")
                    || key.equalsIgnoreCase("Accept-Ranges")
                    || key.equalsIgnoreCase("Content-Range")) {
                for (String v : header.getValue()) {
                    resp.addHeader(key, v);
                }
            }
        }

        // Stream de bytes
        try (InputStream in = conn.getInputStream(); OutputStream out = resp.getOutputStream()) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        }
    }

    private void buscarVideos(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1) Leer el JSON del body y deserializarlo a VideoFilter
        VideoFilter filter = mapper.readValue(req.getReader(), VideoFilter.class);

        // 2) Llamar al endpoint real
        URL url = new URL(API_BASE + "/search");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

        if (status == 200) {
            // devuelve el objeto completo como JSON
            PaginatedResponse<Video> page = mapper.readValue(
                    in,
                    new TypeReference<PaginatedResponse<Video>>() {
            }
            );
            mapper.writeValue(resp.getWriter(), page);

        } else {
            // opcional: propaga el error tal cual vino
            String errorJson = new BufferedReader(new InputStreamReader(in))
                    .lines().collect(Collectors.joining("\n"));
            resp.getWriter().print(errorJson);
        }
    }

    private void actualizarVistas(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String id = req.getParameter("id");
        URL url = new URL(API_BASE + "/" + id + "/views");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        int status = conn.getResponseCode();
        resp.setStatus(status);
    }
}
