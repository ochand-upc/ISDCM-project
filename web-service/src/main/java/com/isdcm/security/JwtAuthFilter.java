/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.security;

import com.isdcm.utils.JwtUtils;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Set;

@Provider
public class JwtAuthFilter implements ContainerRequestFilter {

    // Rutas que dejamos pasar sin token
    private static final Set<String> WHITELIST = Set.of(
        "login",             // POST /api/login
        "openapi.json",      // /api/openapi.json
        "openapi",           // rutas internas de OpenApiResource
        "swagger-ui",        // /api/swagger-ui.html (si lo sirves así)
        "swagger-ui.css",    // ficheros estáticos de la UI
        "swagger-ui-bundle.js",
        "swagger-ui-standalone-preset.js"
    );

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String path = ctx.getUriInfo().getPath();

        // 1) Si la ruta empieza por alguno de los permitidos, salimos sin validar
        for (String pub : WHITELIST) {
            if (path.startsWith(pub)) {
                return;
            }
        }

        // 2) Resto de rutas: exigir Authorization Bearer
        String authHeader = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        String token = authHeader.substring("Bearer ".length());
        try {
            if (!JwtUtils.validate(token)) {
                ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        } catch (Exception e) {
            // cualquier fallo en validación -> 401
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}