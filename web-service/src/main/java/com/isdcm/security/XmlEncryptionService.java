/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.isdcm.security;

import javax.crypto.SecretKey;
import org.w3c.dom.Document;

/**
 * Servicio para encriptar y desencriptar documentos XML.
 */
public interface XmlEncryptionService {

    /**
     * Cifra el XML completo o un fragmento de él.
     *
     * @param plainDoc el DOM/XML en claro
     * @param key la clave AES
     * @return un nuevo Document cifrado
     * @throws Exception si hay error de encriptación
     */
    Document encrypt(Document plainDoc, SecretKey key) throws Exception;

    /**
     * Desencripta un XML previamente cifrado con este servicio.
     *
     * @param encryptedDoc el DOM/XML cifrado
     * @param key la misma clave AES usada en encrypt()
     * @return el Document en claro
     * @throws Exception si hay error de desencriptación
     */
    Document decrypt(Document encryptedDoc, SecretKey key) throws Exception;
}
