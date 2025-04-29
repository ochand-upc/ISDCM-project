/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.isdcm.dao;

import java.util.ArrayList;
import java.util.List;
import com.isdcm.model.Video;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VideoDAO {

    // Método para insertar un video
    public static boolean insertarVideo(Video video) throws SQLException, IOException {
        String sql = "INSERT INTO VIDEOS (TITULO, AUTOR, FECHA, DURACION, REPRODUCCIONES, DESCRIPCION, MIME_TYPE, RUTAVIDEO, TIPO_FUENTE, TAMANO) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return DatabaseExecutor.ejecutarUpdate(sql, video.getTitulo(), video.getAutor(), video.getFecha(),
                                                video.getDuracion(), video.getReproducciones(), video.getDescripcion(),
                                                video.getMimeType(), video.getRutaVideo(), video.getTipoFuente(), 
                                                video.getTamano());
    }

    // Método para listar todos los videos
    public static List<Video> listarVideos() throws SQLException, IOException {
        List<Video> lista = new ArrayList<>();
        String sql = "SELECT * FROM VIDEOS";
        
        try (ResultSet rs = DatabaseExecutor.ejecutarQuery(sql)) {
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
        }
        return lista;
    }

    // Método para verificar si existe un video por título y autor
    public static boolean existeVideo(String titulo, String autor) throws SQLException, IOException {
        String sql = "SELECT ID FROM VIDEOS WHERE TITULO = ? AND AUTOR = ?";
        try (ResultSet rs = DatabaseExecutor.ejecutarQuery(sql, titulo, autor)) {
            return rs.next(); // true si existe un video con el mismo título y autor
        }
    }
    
    // Método para obtener un video por su ID
    public static Video obtenerVideoPorId(int id) throws SQLException, IOException {
        String sql = "SELECT * FROM VIDEOS WHERE ID = ?";

        try (ResultSet rs = DatabaseExecutor.ejecutarQuery(sql, id)) {
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
    
    public static boolean incrementarReproducciones(int id) throws SQLException, IOException {
        String sql = "UPDATE VIDEOS SET REPRODUCCIONES = REPRODUCCIONES + 1 WHERE ID = ?";
        return DatabaseExecutor.ejecutarUpdate(sql, id);
    }
}