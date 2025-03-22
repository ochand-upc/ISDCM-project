/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.minetflix.dao;

import com.isdcm.minetflix.model.Usuario;
import com.isdcm.minetflix.utils.Utils;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    // Método para insertar un usuario
    public static boolean insertarUsuario(Usuario usuario) throws SQLException, IOException {        
        String sql = "INSERT INTO USUARIOS (NOMBRE, APELLIDOS, EMAIL, USERNAME, PASSWORD) "
                   + "VALUES (?, ?, ?, ?, ?)";
        return DatabaseExecutor.ejecutarUpdate(sql, usuario.getNombre(), usuario.getApellidos(),
                                                usuario.getEmail(), usuario.getUsername(), usuario.getPassword());
    }

    // Método para verificar login
    public static boolean validarLogin(String username, String password) throws SQLException, IOException {
        String sql = "SELECT * FROM USUARIOS WHERE USERNAME = ? AND PASSWORD = ?";
        try (ResultSet rs = DatabaseExecutor.ejecutarQuery(sql, username, password)) {
            return rs.next(); // true si encuentra el registro
        }
    }

    // Verifica si el username ya existe
    public static boolean existeUsername(String username) throws SQLException, IOException {
        String sql = "SELECT USERNAME FROM USUARIOS WHERE USERNAME = ?";
        try (ResultSet rs = DatabaseExecutor.ejecutarQuery(sql, username)) {
            return rs.next();
        }
    }
    
    // Verifica si el email ya existe
    public static boolean existeEmail(String email) throws SQLException, IOException {
        String sql = "SELECT USERNAME FROM USUARIOS WHERE EMAIL = ?";
        try (ResultSet rs = DatabaseExecutor.ejecutarQuery(sql, email)) {
            return rs.next();
        }
    }
    
    public static String hashPassword(String password) {
        return Utils.hashString(password);
    }
}