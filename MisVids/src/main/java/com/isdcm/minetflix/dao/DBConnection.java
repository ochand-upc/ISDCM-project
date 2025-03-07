/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.minetflix.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    
    private static Connection conn;
    
    //private static final String PROPERTIES_FILE = "src/main/java/resources/DB.properties";
    private static final String PROPERTIES_FILE = "config/DB.properties";
    
    public static Connection obtenerConexion() throws SQLException, IOException {
        if (conn == null || conn.isClosed()) {
            /*Properties prop = new Properties();
            
            // Cargar las propiedades desde el archivo
            try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
                prop.load(input);
            }
            
            String dbUrl = prop.getProperty("db.url");
            String dbUser = prop.getProperty("db.user");
            String dbPass = prop.getProperty("db.pass");*/
            
            String dbUrl = "jdbc:derby://localhost:1527/MINETFLIX;create=true";
            String dbUser = "pr2";
            String dbPass = "pr2";
            
            // Crear la conexión usando las propiedades
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
        }
        return conn;
    }

    // Método para cerrar la conexión
    public static void cerrarConexion() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
