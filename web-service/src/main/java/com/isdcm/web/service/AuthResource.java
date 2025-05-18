/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.web.service;

import com.isdcm.manager.AuthenticationManager;
import com.isdcm.model.Login;
import com.isdcm.model.VideoFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("/login")
public class AuthResource {

    private final AuthenticationManager authMgr = AuthenticationManager.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Autenticar y obtener JWT"
    )
    @RequestBody(
            description = "Credenciales en JSON\"",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Login.class),
                    examples = @ExampleObject(
                            name = "LoginExample",
                            value = "{"
                            + "\"username\": \"juanp\","
                            + "\"password\": \"03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4\""
                            + "}"
                    )
            )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login correcto",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(example = "{\"token\":\"eyJhbGciOiJI...\"}"))),

        @ApiResponse(
                responseCode = "400",
                description = "Faltan campos o están vacíos",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        examples = @ExampleObject(
                                name = "BadRequest",
                                value = "{\"error\":\"Todos los campos son obligatorios\"}"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Credenciales incorrectas",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        examples = @ExampleObject(
                                name = "Unauthorized",
                                value = "{\"error\":\"Credenciales incorrectas\"}"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Error interno al generar token",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        examples = @ExampleObject(
                                name = "ServerError",
                                value = "{\"error\":\"Error al generar token\"}"
                        )
                )
        )
    })
    public Response login(Login login) {

        if (login == null) {
            JsonObject err = Json.createObjectBuilder()
                    .add("error", "Todos los campos son obligatorios")
                    .build();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(err)
                    .build();
        }
        String username = login.getUsername();
        String password = login.getPassword();

        // 1) Validación de entrada
        if (username == null || username.isEmpty()
                || password == null || password.isEmpty()) {
            JsonObject err = Json.createObjectBuilder()
                    .add("error", "Todos los campos son obligatorios")
                    .build();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(err)
                    .build();
        }

        // 2) Validar credenciales
        try {
            if (!authMgr.validateCredentials(username, password)) {
                JsonObject err = Json.createObjectBuilder()
                        .add("error", "Credenciales incorrectas")
                        .build();
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(err)
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JsonObject err = Json.createObjectBuilder()
                    .add("error", "Error de base de datos")
                    .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(err)
                    .build();
        } catch (IOException ex) {
            ex.printStackTrace();
            JsonObject err = Json.createObjectBuilder()
                    .add("error", "Error al generar token")
                    .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(err)
                    .build();
        }

        // 3) Generar y devolver el JWT
        String token = authMgr.generateToken(username);
        JsonObject body = Json.createObjectBuilder()
                .add("token", token)
                .build();
        return Response.ok(body).build();
    }
}
