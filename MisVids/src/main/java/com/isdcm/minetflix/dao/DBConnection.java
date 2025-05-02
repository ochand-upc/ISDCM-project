package com.isdcm.minetflix.dao;

import com.isdcm.minetflix.utils.AppConfig;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL = AppConfig.get("db.url");
    private static final String DB_USER = AppConfig.get("db.user");
    private static final String DB_PASS = AppConfig.get("db.pass");
    
    public static Connection obtenerConexion() throws SQLException, IOException {
        
        // Crear la conexión usando las propiedades
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
