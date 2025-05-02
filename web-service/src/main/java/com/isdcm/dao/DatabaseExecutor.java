/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.isdcm.dao;

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
             
            // Construir la consulta con los valores reales para debug
            StringBuilder queryDebug = new StringBuilder(sql);
            for (int i = 0; i < parametros.length; i++) {
                String valor = (parametros[i] == null) ? "NULL" : "'" + parametros[i].toString() + "'";
                int pos = queryDebug.indexOf("?");
                if (pos != -1) {
                    queryDebug.replace(pos, pos + 1, valor);
                }
                ps.setObject(i + 1, parametros[i]);
            }

            // Imprimir la consulta generada
            System.out.println("SQL Ejecutado: " + queryDebug.toString());
            // Ejecutar la consulta de actualización
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0; // Si afectó al menos una fila, la operación fue exitosa
        }
    }

    // Método genérico para ejecutar una consulta SELECT
    public static ResultSetWrapper ejecutarQuery(String sql, Object... parametros) throws SQLException, IOException {
        Connection conn = DBConnection.obtenerConexion();
        PreparedStatement ps = conn.prepareStatement(sql);
        
        // Establecer los parámetros de la consulta
        for (int i = 0; i < parametros.length; i++) {
            ps.setObject(i + 1, parametros[i]);
        }
        
        // Ejecutar la consulta y devolver el resultado
        ResultSet rs = ps.executeQuery();
        return new ResultSetWrapper(rs, ps, conn);
    }
    
     /**
     * Wrapper que cierra el ResultSet, su PreparedStatement y la Connection al cerrar().
     */
    public static class ResultSetWrapper implements AutoCloseable {
        private final ResultSet rs;
        private final PreparedStatement ps;
        private final Connection conn;
        
        public ResultSetWrapper(ResultSet rs, PreparedStatement ps, Connection conn) {
            this.rs   = rs;
            this.ps   = ps;
            this.conn = conn;
        }
        
        public ResultSet getResultSet() {
            return rs;
        }
        
        @Override
        public void close() throws SQLException {
            rs.close();
            ps.close();
            //conn.close();
        }
    }
}
