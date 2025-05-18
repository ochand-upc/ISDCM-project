/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.security;

import com.isdcm.utils.Utils;
import javax.crypto.SecretKey;
import org.apache.xml.security.Init;
import org.apache.xml.security.encryption.XMLCipher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implementación de XmlEncryptionService usando AES-128 con Apache Santuario,
 *
 */
public class AesXmlEncryptionService implements XmlEncryptionService {

    static {
        // Inicializar la librería de Apache Santuario
        Init.init();
    }

    @Override
    public Document encrypt(Document plainDoc, SecretKey unused) throws Exception {
        // 1) Obtener key desde Utils
        SecretKey symmetricKey = Utils.getSecretKey();

        // 2) Instanciar XMLCipher para AES-128
        XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.AES_128);
        xmlCipher.init(XMLCipher.ENCRYPT_MODE, symmetricKey);

        // 3) Cifrar todo el documento (encryptContentsOnly = false)
        Element root = plainDoc.getDocumentElement();
        xmlCipher.doFinal(plainDoc, root, false);

        return plainDoc;
    }

    @Override
    public Document decrypt(Document encryptedDoc, SecretKey unused) throws Exception {
        // 1) Obtener key desde Utils
        SecretKey symmetricKey = Utils.getSecretKey();

        // 2) Instanciar XMLCipher para AES-128
        XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.AES_128);
        xmlCipher.init(XMLCipher.DECRYPT_MODE, symmetricKey);

        // 3) Desencriptar todo el nodo raíz
        Element root = encryptedDoc.getDocumentElement();
        Document plainDoc = xmlCipher.doFinal(encryptedDoc, root);

        return plainDoc;
    }
}
