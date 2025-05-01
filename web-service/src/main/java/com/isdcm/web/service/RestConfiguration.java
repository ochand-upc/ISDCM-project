package com.isdcm.web.service;

import io.swagger.v3.jaxrs2.SwaggerSerializers;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
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