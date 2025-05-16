/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.security;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;

public class AesFileEncryptionService implements FileEncryptionService {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH   = 16;

    @Override
    public void encrypt(File input, File output, SecretKey key) throws Exception {
        try (
            FileInputStream  fis = new FileInputStream(input);
            FileOutputStream fos = new FileOutputStream(output)
        ) {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // escribe IV al principio
            fos.write(cipher.getIV());
            try (CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
                byte[] buf = new byte[8*1024];
                int r;
                while ((r = fis.read(buf)) != -1) {
                    cos.write(buf, 0, r);
                }
            }
        }
    }

    @Override
    public void decrypt(File input, File output, SecretKey key) throws Exception {
        try (
            FileInputStream  fis = new FileInputStream(input);
            FileOutputStream fos = new FileOutputStream(output)
        ) {
            byte[] iv = new byte[IV_LENGTH];
            if (fis.read(iv) != IV_LENGTH) {
                throw new IOException("IV incompleto");
            }
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            try (CipherInputStream cis = new CipherInputStream(fis, cipher)) {
                byte[] buf = new byte[8*1024];
                int r;
                while ((r = cis.read(buf)) != -1) {
                    fos.write(buf, 0, r);
                }
            }
        }
    }
}