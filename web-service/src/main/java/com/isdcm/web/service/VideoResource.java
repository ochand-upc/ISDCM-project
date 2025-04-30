/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.web.service;

import com.isdcm.dao.VideoDAO;
import com.isdcm.model.Video;
import com.isdcm.model.VideoFilter;
import com.isdcm.utils.VideoPlaybackManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Path("/videos")
@Produces(MediaType.APPLICATION_JSON)
public class VideoResource {

    /**
     * Incrementa en 1 el contador de reproducciones del video con {id}.
     * Método HTTP: PUT
     * URL: /{context}/api/videos/{id}/reproducir
     */
    @PUT
    @Path("/{id}/reproducciones")
    public Response registrarReproducciones(@PathParam("id") int id) {
        try {
            boolean ok = VideoPlaybackManager.registrarReproduccion(id);
            if (ok) {
                // Devolvemos un JSON sencillo con estado
                return Response
                        .ok("{\"success\":true,\"id\":" + id + "}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            } else {
                // Si no existe el video, se retorna 404
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Video no encontrado\",\"id\":" + id + "}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 500 en caso de error interno
            return Response
                    .serverError()
                    .entity("{\"error\":\"Error interno al actualizar reproducciones.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    /**
     * 
     * @param id
     * @param rangeHeader
     * @return 
     */
    @GET
    @Path("/{id}/stream")
    public Response streamVideo(
        @PathParam("id") int id,
        @HeaderParam("Range") String rangeHeader) {

            try {
                StreamingOutput stream = VideoPlaybackManager.streamLocalVideo(id, rangeHeader);
                String mime   = VideoPlaybackManager.detectMimeType(id);
                long   length = VideoPlaybackManager.getFileLength(id);

                long from  = 0;
                long to    = length - 1;
                boolean partial = false;

                if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                    partial = true;
                    String payload = rangeHeader.substring(6);      // "0-" o "123-456"
                    String[] parts = payload.split("-", 2);         // máximo 2 trozos
                    from = Long.parseLong(parts[0]);
                    if (parts.length == 2 && !parts[1].isEmpty()) {
                        to = Long.parseLong(parts[1]);
                    }
                    if (to >= length) {
                        to = length - 1;
                    }
                }

                long chunk = to - from + 1;

                Response.ResponseBuilder rb = partial
                    ? Response.status(Response.Status.PARTIAL_CONTENT)
                    : Response.ok();

                return rb
                    .entity(stream)
                    .header("Accept-Ranges", "bytes")
                    .header("Content-Type",    mime)
                    .header("Content-Length",  chunk)
                    .header("Content-Range",   String.format("bytes %d-%d/%d", from, to, length))
                    .build();

            } catch (IllegalArgumentException ia) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\":\""+ia.getMessage()+"\"}")
                               .build();
            } catch (Exception e) {
                e.printStackTrace();
                return Response.serverError().build();
            }
    }
    
     /**
     * Filtrar vídeos con criterios y paginación.
     * POST /api/videos/search
     */
    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response buscarVideos(VideoFilter filter) {
        try {
            List<Video> resultados = VideoDAO.buscarVideos(
                filter.getTitulo(),
                filter.getAutor(),
                filter.getFecha(),
                filter.getPage(),
                filter.getPageSize()
            );
            // Para asegurarnos de serializar correctamente la lista:
            GenericEntity<List<Video>> entity =
                new GenericEntity<>(resultados){};
            return Response.ok(entity).build();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"No se pudo buscar videos.\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
        }
    }
}