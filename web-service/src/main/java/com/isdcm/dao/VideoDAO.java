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
import java.util.ArrayList;
import java.util.List;

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
    

    /**
     * Busca vídeos con filtros y paginación.
     */
    public static List<Video> buscarVideos(String titulo, String autor, String fecha, int page, int pageSize)
            throws SQLException, IOException {

        StringBuilder sql = new StringBuilder("SELECT * FROM VIDEOS");
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;

        if (titulo != null && !titulo.isEmpty()) {
            sql.append(hasWhere ? " AND" : " WHERE");
            sql.append(" LOWER(TITULO) LIKE ?");
            params.add("%" + titulo.toLowerCase() + "%");
            hasWhere = true;
        }
        if (autor != null && !autor.isEmpty()) {
            sql.append(hasWhere ? " AND" : " WHERE");
            sql.append(" LOWER(AUTOR) LIKE ?");
            params.add("%" + autor.toLowerCase() + "%");
            hasWhere = true;
        }
        if (fecha != null && !fecha.isEmpty()) {
            sql.append(hasWhere ? " AND" : " WHERE");
            sql.append(" FECHA LIKE ?");
            params.add(fecha + "%");
        }

        // paginación
        sql.append(" ORDER BY FECHA DESC")
           .append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (DatabaseExecutor.ResultSetWrapper wrap =
                 DatabaseExecutor.ejecutarQuery(sql.toString(), params.toArray());
             ResultSet rs = wrap.getResultSet()) {

            List<Video> lista = new ArrayList<>();
            while (rs.next()) {
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
                lista.add(v);
            }
            return lista;
        }
    }
    
    public static int countVideos(String titulo, String autor, String fecha)
            throws SQLException, IOException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM VIDEOS");
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;

        if (titulo != null && !titulo.isEmpty()) {
            sql.append(hasWhere ? " AND" : " WHERE");
            sql.append(" LOWER(TITULO) LIKE ?");
            params.add("%" + titulo.toLowerCase() + "%");
            hasWhere = true;
        }
        if (autor != null && !autor.isEmpty()) {
            sql.append(hasWhere ? " AND" : " WHERE");
            sql.append(" LOWER(AUTOR) LIKE ?");
            params.add("%" + autor.toLowerCase() + "%");
            hasWhere = true;
        }
        if (fecha != null && !fecha.isEmpty()) {
            sql.append(hasWhere ? " AND" : " WHERE");
            sql.append(" FECHA LIKE ?");
            params.add(fecha + "%");
        }

        try (DatabaseExecutor.ResultSetWrapper wrap =
                 DatabaseExecutor.ejecutarQuery(sql.toString(), params.toArray());
             ResultSet rs = wrap.getResultSet()) {
            rs.next();
            return rs.getInt(1);
        }
    }
}