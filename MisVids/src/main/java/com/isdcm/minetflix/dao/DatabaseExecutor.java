/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.isdcm.minetflix.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseExecutor {
    
    // Método genérico para ejecutar una actualización (INSERT, UPDATE, DELETE)
    public static boolean ejecutarUpdate(String sql, Object... parametros) throws SQLException, IOException {
        try (Connection conn = DBConnection.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            // Establecer los parámetros de la consulta
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            
            // Ejecutar la consulta de actualización
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0; // Si afectó al menos una fila, la operación fue exitosa
        }
    }

    // Método genérico para ejecutar una consulta SELECT
    public static ResultSet ejecutarQuery(String sql, Object... parametros) throws SQLException, IOException {
        Connection conn = DBConnection.obtenerConexion();
        PreparedStatement ps = conn.prepareStatement(sql);
        
        // Establecer los parámetros de la consulta
        for (int i = 0; i < parametros.length; i++) {
            ps.setObject(i + 1, parametros[i]);
        }
        
        // Ejecutar la consulta y devolver el resultado
        return ps.executeQuery();
    }
}
