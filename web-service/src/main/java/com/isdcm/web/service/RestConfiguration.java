package com.isdcm.web.service;

import io.swagger.v3.jaxrs2.SwaggerSerializers;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
@OpenAPIDefinition(
  info = @Info(
    title       = "MiNetflix API",
    version     = "1.0",
    description = "Documentación OpenAPI de MiNetflix"
  ),
  servers = {
    // Swagger-UI usará esta URL base para todas las peticiones
    @Server(url = "/web-service", description = "Contexto local de GlassFish")
  }
)
public class RestConfiguration extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> clases = new HashSet<>();
        clases.add(VideoResource.class);
        
        // Swagger / OpenAPI
        clases.add(OpenApiResource.class);
        clases.add(SwaggerSerializers.class);
        return clases;
    }
}