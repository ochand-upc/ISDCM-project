/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.minetflix.dao;

import java.sql.*;
import com.isdcm.minetflix.model.Usuario;

public class UsuarioDAO {
    
    // Cambia la URL, usuario y contraseña según tu configuración
    private static final String DB_URL = "jdbc:derby://localhost:1527/MINETFLIX;create=true";
    private static final String DB_USER = "pr2";
    private static final String DB_PASS = "pr2";

    // Método para insertar un usuario
    public static boolean insertarUsuario(Usuario usuario) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            // Verificar si ya existe username
            if (existeUsername(conn, usuario.getUsername())) {
                return false; // Ya existe el usuario
            }
            String sql = "INSERT INTO USUARIOS (NOMBRE, APELLIDOS, EMAIL, USERNAME, PASSWORD) "
                       + "VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getUsername());
            ps.setString(5, usuario.getPassword());
            ps.executeUpdate();
            return true;
        } finally {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
    }

    // Método para verificar login
    public static boolean validarLogin(String username, String password) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String sql = "SELECT * FROM USUARIOS WHERE USERNAME = ? AND PASSWORD = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            return rs.next(); // true si encuentra registro
         } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
    }

    // Verifica si el username ya existe
    private static boolean existeUsername(Connection conn, String username) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT USERNAME FROM USUARIOS WHERE USERNAME = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }
}
