/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.minetflix.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {
    private static final String PROPERTIES_FILE = System.getProperty("config.path");
    private static final Properties props = new Properties();

    static {
        try (InputStream in = new FileInputStream(PROPERTIES_FILE)) {
            props.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                "No se pudo leer el fichero de configuración: " + PROPERTIES_FILE
            );
        }
    }

    private AppConfig() { }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
