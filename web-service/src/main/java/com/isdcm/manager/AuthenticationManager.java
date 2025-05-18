/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.manager;

import com.isdcm.dao.UsuarioDAO;
import com.isdcm.utils.JwtUtils;
import java.io.IOException;
import java.sql.SQLException;

public class AuthenticationManager {

    private static AuthenticationManager instance;

    private AuthenticationManager() { }

    public static synchronized AuthenticationManager getInstance() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }

    /**
     * Valida las credenciales llamando a UsuarioDAO (ya aplica hash).
     * @return true si usuario+password son correctos
     */
    public boolean validateCredentials(String username, String password) 
            throws SQLException, IOException {
        return UsuarioDAO.validarLogin(username, password);
    }

    /**
     * Genera un JWT para el usuario dado, usando JwtUtils.
     */
    public String generateToken(String username) {
        return JwtUtils.generateToken(username);
    }
}