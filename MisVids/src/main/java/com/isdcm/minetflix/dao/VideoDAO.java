/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.minetflix.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.isdcm.minetflix.model.Video;

public class VideoDAO {

    // Ajusta la URL, usuario y contraseña a tu entorno    
    private static final String DB_URL = "jdbc:derby://localhost:1527/MINETFLIX;create=true";
    private static final String DB_USER = "pr2";
    private static final String DB_PASS = "pr2";

    /**
     * Inserta un nuevo registro en la tabla VIDEOS.
     * @param video Objeto Video con los datos a insertar
     * @return true si la inserción fue exitosa, false si el video ya existe o hubo error
     * @throws SQLException 
     */
    public static boolean insertarVideo(Video video) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            // Si quieres evitar duplicados por Título + Autor, descomenta y usa el método existeVideo
            // if (existeVideo(conn, video.getTitulo(), video.getAutor())) {
            //     return false;
            // }

            String sql = "INSERT INTO VIDEOS (TITULO, AUTOR, FECHA, DURACION, REPRODUCCIONES, DESCRIPCION, FORMATO, RUTAVIDEO) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, video.getTitulo());
            ps.setString(2, video.getAutor());
            ps.setString(3, video.getFecha());
            ps.setString(4, video.getDuracion());
            ps.setInt(5, video.getReproducciones());
            ps.setString(6, video.getDescripcion());
            ps.setString(7, video.getFormato());
            ps.setString(8, video.getRutavideo());

            ps.executeUpdate();
            return true;
        } finally {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
    }

    /**
     * Devuelve una lista con todos los registros de la tabla VIDEOS.
     * @return List<Video> con los videos encontrados
     * @throws SQLException 
     */
    public static List<Video> listarVideos() throws SQLException {
        List<Video> lista = new ArrayList<>();
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM VIDEOS");

            while (rs.next()) {
                Video v = new Video();
                v.setId(rs.getInt("ID"));
                v.setTitulo(rs.getString("TITULO"));
                v.setAutor(rs.getString("AUTOR"));
                v.setFecha(rs.getString("FECHA"));
                v.setDuracion(rs.getString("DURACION"));
                v.setReproducciones(rs.getInt("REPRODUCCIONES"));
                v.setDescripcion(rs.getString("DESCRIPCION"));
                v.setFormato(rs.getString("FORMATO"));
                v.setRutavideo(rs.getString("RUTAVIDEO"));

                lista.add(v);
            }
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (conn != null) conn.close();
        }

        return lista;
    }

    /**
     * Comprueba si existe un vídeo en la base de datos con el mismo título y autor.
     * (Este método es opcional, depende de tu lógica de negocio)
     * @param conn Conexión activa a la base de datos
     * @param titulo Título del vídeo
     * @param autor Autor del vídeo
     * @return true si existe, false en caso contrario
     * @throws SQLException 
     */
    private static boolean existeVideo(Connection conn, String titulo, String autor) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT ID FROM VIDEOS WHERE TITULO = ? AND AUTOR = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, titulo);
            ps.setString(2, autor);
            rs = ps.executeQuery();
            return rs.next();  // true si encuentra al menos un registro
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }
}
