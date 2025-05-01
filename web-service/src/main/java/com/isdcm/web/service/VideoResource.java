/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.web.service;

import com.isdcm.dao.VideoDAO;
import com.isdcm.model.PaginatedResponse;
import com.isdcm.model.Video;
import com.isdcm.model.VideoFilter;
import com.isdcm.utils.VideoPlaybackManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
     * Incrementa en 1 el contador de visualizaciones del video con {id}.
     * Método HTTP: PUT
     * URL: /{context}/api/videos/{id}/reproducir
     */
    @Operation(summary = "Incrementa contador de vistas")
    @ApiResponses({
      @ApiResponse(responseCode = "200",
        description = "Reproducción registrada",
        content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          examples = @ExampleObject(name  = "SuccessExampleExample", value = "{\"success\":true,\"id\":5}")
        )
      ),
      @ApiResponse(responseCode = "404",
        description = "Vídeo no encontrado",
        content = @Content(
        mediaType = MediaType.APPLICATION_JSON,
        examples = @ExampleObject(name  = "NotFoundExample",
                        value = "{\"error\":\"Video no encontrado\",\"id\":5}")
        )
      ),
      @ApiResponse(responseCode = "500",
        description = "Error interno",
        content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          examples = @ExampleObject(name  = "ServerErrorExample",
                            value = "{\"error\":\"Error interno al actualizar reproducciones.\"}")
        )
      )
    })
    @PUT
    @Path("/{id}/views")
    public Response registrarVistas(@PathParam("id") int id) {
        try {
            boolean ok = VideoPlaybackManager.registrarVisualizacion(id);
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
    @Operation(summary = "Stream de video almancenado en base de datos")
    @ApiResponses({
      @ApiResponse(
        responseCode = "200",
        description  = "Stream completo del vídeo",
        content = @Content(
          mediaType = "video/mp4",
          schema    = @Schema(type = "string", format = "binary")
        )
      ),
      @ApiResponse(
        responseCode = "206",
        description  = "Stream parcial (rango solicitado)",
        content = @Content(
          mediaType = "video/mp4",
          schema    = @Schema(type = "string", format = "binary")
        ),
        headers = @Header(
          name        = "Content-Range",
          description = "rango enviado / total bytes",
          schema      = @Schema(type = "string", example = "bytes 0-1023/2048")
        )
      ),
      @ApiResponse(
        responseCode = "404",
        description  = "Vídeo no encontrado",
        content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          examples = @ExampleObject(
            name  = "NotFoundExample",
            value = "{\"error\":\"Video no encontrado\",\"id\":5}"
          )
        )
      ),
      @ApiResponse(
        responseCode = "500",
        description  = "Error interno del servidor",
        content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          examples = @ExampleObject(
            name  = "ServerErrorExample",
            value = "{\"error\":\"Error durante el streaming del video.\"}"
          )
        )
      )
    })
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
    @Operation(summary = "Filtros para listado")
    @RequestBody(
      description = "Objeto con filtros (título, autor, fecha, orden, página, tamaño)",
      required    = true,
      content     = @Content(
        mediaType = MediaType.APPLICATION_JSON,
        schema    = @Schema(implementation = VideoFilter.class),
        examples  = @ExampleObject(
          name  = "FilterExample",
          value = "{\n" +
                  "  \"titulo\": \"Java\",\n" +
                  "  \"autor\": \"Ana\",\n" +
                  "  \"fecha\": \"2025-05-01\",\n" +
                  "  \"sortField\": \"titulo\",\n" +
                  "  \"sortOrder\": \"asc\",\n" +
                  "  \"page\": 1,\n" +
                  "  \"pageSize\": 10\n" +
                  "}"
        )
      )
    )
    @ApiResponses({
      @ApiResponse(
        responseCode = "200",
        description  = "Listado paginado de vídeos",
        content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema    = @Schema(implementation = PaginatedResponse.class),
          examples  = @ExampleObject(
            name  = "PaginatedExample",
            value = "{\n" +
                    "  \"total\": 42,\n" +
                    "  \"items\": [\n" +
                    "    {\"id\":1,\"titulo\":\"Mi vídeo\",\"autor\":\"Ana\",\"fecha\":\"2025-04-30\"}\n" +
                    "  ]\n" +
                    "}"
          )
        )
      ),
      @ApiResponse(
        responseCode = "500",
        description  = "Error interno al buscar vídeos",
        content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          examples = @ExampleObject(
            name  = "SearchErrorExample",
            value = "{\"error\":\"No se pudo buscar videos.\"}"
          )
        )
      )
    })
    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarVideos(VideoFilter filter) {
        try {
            int total = VideoDAO.countVideos(
                filter.getTitulo(),
                filter.getAutor(),
                filter.getFecha()
            );
            List<Video> items = VideoDAO.buscarVideos(
                filter.getTitulo(),
                filter.getAutor(),
                filter.getFecha(),
                filter.getSortField(),
                filter.getSortOrder(),
                filter.getPage(),
                filter.getPageSize()
            );
            PaginatedResponse<Video> page = new PaginatedResponse<>(total, items);
            GenericEntity<PaginatedResponse<Video>> entity =
                new GenericEntity<>(page){};
            return Response.ok(entity).build();
        } catch(SQLException|IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\":\"No se pudo buscar videos.\"}")
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        }
    }
}