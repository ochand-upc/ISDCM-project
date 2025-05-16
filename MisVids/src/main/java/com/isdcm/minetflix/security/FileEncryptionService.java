package com.isdcm.minetflix.security;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

import java.io.File;
import javax.crypto.SecretKey;

public interface FileEncryptionService {
    /**
     * Cifra el fichero de entrada y escribe el resultado en output.
     * @param input  fichero original (en claro)
     * @param output fichero de destino (cifrado)
     * @param key    clave simétrica AES
     */
    void encrypt(File input, File output, SecretKey key) throws Exception;

    /**
     * Desencripta el fichero de entrada y escribe el resultado en output.
     * @param input  fichero cifrado
     * @param output fichero de destino (descifrado)
     * @param key    misma clave simétrica usada en encrypt()
     */
    void decrypt(File input, File output, SecretKey key) throws Exception;
}