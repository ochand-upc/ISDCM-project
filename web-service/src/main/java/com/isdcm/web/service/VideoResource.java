/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.web.service;

import com.isdcm.dao.VideoDAO;
import com.isdcm.model.PaginatedResponse;
import com.isdcm.model.Video;
import com.isdcm.model.VideoFilter;
import com.isdcm.manager.SearchManager;
import com.isdcm.manager.VideoPlaybackManager;
import com.isdcm.security.AesXmlEncryptionService;
import com.isdcm.security.XmlEncryptionService;
import com.isdcm.utils.Utils;
import com.isdcm.utils.XmlDocumentBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


@Path("/videos")
@Produces(MediaType.APPLICATION_JSON)
public class VideoResource {
    
        private final XmlEncryptionService xmlEncSvc = new AesXmlEncryptionService();


    /**
     * Incrementa en 1 el contador de visualizaciones del video con {id}. Método
     * HTTP: PUT URL: /web-service/api/videos/{id}/views
     */
    @Operation(summary = "Incrementa contador de vistas")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                description = "Reproducción registrada",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        examples = @ExampleObject(name = "SuccessExampleExample", value = "{\"success\":true,\"id\":5}")
                )
        ),
        @ApiResponse(responseCode = "404",
                description = "Vídeo no encontrado",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        examples = @ExampleObject(name = "NotFoundExample",
                                value = "{\"error\":\"Video no encontrado\",\"id\":5}")
                )
        ),
        @ApiResponse(responseCode = "500",
                description = "Error interno",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        examples = @ExampleObject(name = "ServerErrorExample",
                                value = "{\"error\":\"Error interno al actualizar reproducciones.\"}")
                )
        )
    })
    @PUT
    @Path("/{id}/views")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
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
    @Operation(summary = "Stream de video almancenado localmente")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Stream completo del vídeo",
                content = @Content(
                        mediaType = "video/mp4",
                        schema = @Schema(type = "string", format = "binary")
                )
        ),
        @ApiResponse(
                responseCode = "206",
                description = "Stream parcial (rango solicitado)",
                content = @Content(
                        mediaType = "video/mp4",
                        schema = @Schema(type = "string", format = "binary")
                ),
                headers = @Header(
                        name = "Content-Range",
                        description = "rango enviado / total bytes",
                        schema = @Schema(type = "string", example = "bytes 0-1023/2048")
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Vídeo no encontrado",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        examples = @ExampleObject(
                                name = "NotFoundExample",
                                value = "{\"error\":\"Video no encontrado\",\"id\":5}"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        examples = @ExampleObject(
                                name = "ServerErrorExample",
                                value = "{\"error\":\"Error durante el streaming del video.\"}"
                        )
                )
        )
    })
    @GET
    @Path("/{id}/stream")
    @Produces("video/mp4")
    public Response streamVideo(
            @PathParam("id") int id,
            @Parameter(in = ParameterIn.HEADER, name = "Range",
                    description = "Rango de bytes solicitado, p.ej. bytes=0-1023", required = false,
                    example = "bytes=0-1023")
            @HeaderParam("Range") String rangeHeader) {

        try {
            // 1) Recuperar metadata del vídeo
            Video v = VideoDAO.obtenerVideoPorId(id);
            if (v == null || !"LOCAL".equals(v.getTipoFuente())) {
                return Response.status(Status.NOT_FOUND)
                        .entity("Vídeo no válido o no encontrado")
                        .build();
            }

            // 2) Generar el stream desencriptado (on-the-fly)
            StreamingOutput stream = VideoPlaybackManager.streamLocalVideo(id, rangeHeader);

            // 3) Construir la respuesta con headers
            Response.ResponseBuilder rb = Response.ok(stream)
                    .type(v.getMimeType())
                    .header("Accept-Ranges", "bytes");

            // Si vino Range, devolvemos 206 Partial Content
            if (rangeHeader != null) {
                rb.status(Status.PARTIAL_CONTENT);
            }

            return rb.build();

        } catch (IllegalArgumentException iae) {
            // Bad request para parámetros inválidos
            return Response.status(Status.BAD_REQUEST)
                    .entity(iae.getMessage())
                    .build();

        } catch (Exception e) {
            // Error inesperado durante I/O o desencriptado
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al procesar el streaming: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Filtrar vídeos con criterios y paginación. POST /api/videos/search
     */
    @Operation(summary = "Filtros para listado")
    @RequestBody(
            description = "Objeto con filtros (título, autor, fecha, orden, página, tamaño)",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = VideoFilter.class),
                    examples = @ExampleObject(
                            name = "FilterExample",
                            value = "{\n"
                            + "  \"titulo\": \"\",\n"
                            + "  \"autor\": \"\",\n"
                            + "  \"fecha\": \"2025-05-01\",\n"
                            + "  \"sortField\": \"fecha\",\n"
                            + "  \"sortOrder\": \"asc\",\n"
                            + "  \"page\": 1,\n"
                            + "  \"pageSize\": 5\n"
                            + "}"
                    )
            )
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Listado paginado de vídeos",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = PaginatedResponse.class),
                        examples = @ExampleObject(
                                name = "PaginatedExample",
                                value = "{\n"
                                + "  \"total\": 42,\n"
                                + "  \"items\": [\n"
                                + "    {\"id\":1,\"titulo\":\"Mi vídeo\",\"autor\":\"Ana\",\"fecha\":\"2025-04-30\"}\n"
                                + "  ]\n"
                                + "}"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Error interno al buscar vídeos",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        examples = @ExampleObject(
                                name = "SearchErrorExample",
                                value = "{\"error\":\"No se pudieron encontrar videos.\"}"
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
            GenericEntity<PaginatedResponse<Video>> entity
                    = SearchManager.getPaginatedResponse(filter);
            return Response.ok(entity).build();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"No se pudieron encontrar videos.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    /**
     * Obtener vídeo con por id GET /api/{id}
     */
    @Operation(summary = "Retorna video a partir de un id")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Listado paginado de vídeos",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = Video.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Vídeo no encontrado",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        examples = @ExampleObject(
                                name = "NotFoundExample",
                                value = "{\"error\":\"Video no encontrado\",\"id\":5}"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Error interno al buscar vídeo",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        examples = @ExampleObject(
                                name = "SearchErrorExample",
                                value = "{\"error\":\"No se pudo encontrar el video.\"}"
                        )
                )
        )
    })
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVideo(@PathParam("id") int id) {
        try {
            Video v = VideoDAO.obtenerVideoPorId(id);
            if (v == null) {
                return Response.status(404).build();
            }
            return Response.ok(v).build();
        } catch (SQLException | IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"No se pudo encontrar el video.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    @GET
    @Path("{id}/metadata/encrypted")
    @Produces(MediaType.APPLICATION_XML)
    @Operation(
        summary = "Descargar metadatos cifrados",
        description = "Genera el XML DIDL, lo cifra con XML Encryption y lo devuelve como adjunto"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Metadatos DIDL cifrado",
            content = @Content(mediaType = MediaType.APPLICATION_XML,
                               schema = @Schema(type = "string", format = "xml"),
                               examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                   name  = "EncryptedMetadata",
                                   value = "<xenc:EncryptedData>…</xenc:EncryptedData>"
                               ))),
        @ApiResponse(responseCode = "404", description = "Vídeo no encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_XML,
                               examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                   name  = "NotFound",
                                   value = "<error>Vídeo no encontrado</error>"
                               ))),
        @ApiResponse(responseCode = "500", description = "Error al cifrar metadatos",
            content = @Content(mediaType = MediaType.APPLICATION_XML,
                               examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                   name  = "ServerError",
                                   value = "<error>Imposible encriptar metadatos</error>"
                               )))
    })
    public Response getMetadataEncrypted(@PathParam("id") int id) {
        try {
            Video v = VideoDAO.obtenerVideoPorId(id);
            if (v == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("<error>Vídeo no encontrado</error>")
                               .type(MediaType.APPLICATION_XML)
                               .build();
            }

            Document plain = XmlDocumentBuilder.buildDocument(v);
            Document enc   = xmlEncSvc.encrypt(plain, Utils.getSecretKey());
            StreamingOutput so = out -> {
                try {
                    Transformer tf = TransformerFactory.newInstance().newTransformer();
                    tf.setOutputProperty(OutputKeys.INDENT, "yes");
                    tf.transform(new DOMSource(enc), new StreamResult(out));
                } catch (TransformerException e) {
                    throw new IOException("Error al serializar metadatos cifrados", e);
                }
            };

            return Response.ok(so)
                           .header("Content-Disposition", "attachment; filename=\"video" + id + ".metadata.xml.enc\"")
                           .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("<error>Imposible encriptar metadatos</error>")
                           .type(MediaType.APPLICATION_XML)
                           .build();
        }
    }
    
    @POST
    @Path("{id}/metadata/decrypt")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Operation(summary = "Desencriptar metadatos",
               description = "Recibe un XML cifrado en el body y lo desencripta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Metadatos DIDL desencriptado",
            content = @Content(mediaType = MediaType.APPLICATION_XML,
                               schema = @Schema(type = "string", format = "xml"))),
        @ApiResponse(responseCode = "400", description = "XML inválido o mal formado",
            content = @Content(mediaType = MediaType.APPLICATION_XML)),
        @ApiResponse(responseCode = "500", description = "Error al desencriptar metadatos",
            content = @Content(mediaType = MediaType.APPLICATION_XML))
    })
    public Response postMetadataDecrypt(@PathParam("id") int id, InputStream encryptedXml) {
        try {
            if (VideoDAO.obtenerVideoPorId(id) == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("<error>Vídeo no encontrado</error>")
                               .type(MediaType.APPLICATION_XML)
                               .build();
            }
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document encDoc = db.parse(encryptedXml);

            Document plainDoc = xmlEncSvc.decrypt(encDoc, Utils.getSecretKey());

            StreamingOutput so = out -> {
                try {
                    Transformer tf = TransformerFactory.newInstance().newTransformer();
                    tf.setOutputProperty(OutputKeys.INDENT, "yes");
                    tf.transform(new DOMSource(plainDoc), new StreamResult(out));
                } catch (TransformerException e) {
                    throw new IOException("Error al serializar XML desencriptado", e);
                }
            };
            return Response.ok(so).build();
        } catch (SAXException | ParserConfigurationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("<error>XML inválido o mal formado</error>")
                           .type(MediaType.APPLICATION_XML)
                           .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("<error>Imposible desencriptar metadatos</error>")
                           .type(MediaType.APPLICATION_XML)
                           .build();
        }
    }
    
}
