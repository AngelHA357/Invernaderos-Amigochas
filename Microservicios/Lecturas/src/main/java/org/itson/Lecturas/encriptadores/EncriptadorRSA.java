/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.itson.Lecturas.encriptadores;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import javax.crypto.Cipher;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 *
 * @author ricar
 */
public class EncriptadorRSA {

    static {
        Security.addProvider(new BouncyCastleProvider()); // Necesario para BouncyCastle
    }
    
    // Cargar clave pública desde archivo PEM
    public static PublicKey loadPublicKey(String filepath) throws Exception {
        try (PemReader reader = new PemReader(new FileReader(filepath))) {
            PemObject pemObject = reader.readPemObject();
            byte[] content = pemObject.getContent();
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(content);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        }
    }

    // Cargar clave privada desde archivo PEM
    public static PrivateKey loadPrivateKey(InputStream filepath) throws Exception {
        try (PemReader reader = new PemReader(new InputStreamReader(filepath))) {
            PemObject pemObject = reader.readPemObject();
            byte[] content = pemObject.getContent();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        }
    }

    // Encriptar con clave pública
    public static byte[] encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    }

    // Desencriptar con clave privada
    public static String decrypt(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypted = cipher.doFinal(encryptedData);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

}
