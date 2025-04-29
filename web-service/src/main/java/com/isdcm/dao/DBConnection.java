package com.isdcm.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    
    private static Connection conn;
    
    private static final String PROPERTIES_FILE = System.getProperty("config.path");

    
    public static Connection obtenerConexion() throws SQLException, IOException {
        if (conn == null || conn.isClosed()) {
            Properties prop = new Properties();
            
            // Cargar las propiedades desde el archivo
            try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
                prop.load(input);
            }
            
            String dbUrl = prop.getProperty("db.url");
            String dbUser = prop.getProperty("db.user");
            String dbPass = prop.getProperty("db.pass");
            
            
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
