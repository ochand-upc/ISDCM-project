/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.isdcm.dao;

import com.isdcm.dao.DatabaseExecutor.ResultSetWrapper;
import com.isdcm.model.Video;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VideoDAO {

    // Método para obtener un video por su ID
    public static Video obtenerVideoPorId(int id) throws SQLException, IOException {
        String sql = "SELECT * FROM VIDEOS WHERE ID = ?";

        try (ResultSetWrapper wrap = DatabaseExecutor.ejecutarQuery(sql, id);
                ResultSet rs = wrap.getResultSet()) {
            
            if (rs.next()) {
                
                Video v = new Video();
                v.setId(rs.getInt("ID"));
                v.setTitulo(rs.getString("TITULO"));
                v.setAutor(rs.getString("AUTOR"));
                v.setFecha(rs.getString("FECHA"));
                v.setDuracion(rs.getDouble("DURACION"));
                v.setReproducciones(rs.getInt("REPRODUCCIONES"));
                v.setDescripcion(rs.getString("DESCRIPCION"));
                v.setMimeType(rs.getString("MIME_TYPE"));
                v.setRutaVideo(rs.getString("RUTAVIDEO"));
                v.setTipoFuente(rs.getString("TIPO_FUENTE"));
                v.setTamano(rs.getLong("TAMANO"));
                return v;
            }
        }
        return null;
    }
    
    // Método para incrementar reproducciones de un video por su ID
    public static boolean incrementarReproducciones(int id) throws SQLException, IOException {
        String sql = "UPDATE VIDEOS SET REPRODUCCIONES = REPRODUCCIONES + 1 WHERE ID = ?";
        return DatabaseExecutor.ejecutarUpdate(sql, id);
    }
}