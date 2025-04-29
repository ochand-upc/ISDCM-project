package com.isdcm.web.service;

import com.isdcm.web.service.resources.JakartaEE10Resource;
import jakarta.ws.rs.ApplicationPath;
import java.util.Set;
import java.util.HashSet;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestConfiguration extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> clases = new HashSet<>();
        // registra aquí TODOS tus recursos REST
        clases.add(JakartaEE10Resource.class);
        clases.add(VideoResource.class);
        return clases;
    }
}
