/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.itson.Anomalyzer.encriptadores;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 *
 * @author ricar
 */
public class EncriptadorRSA {

    static {
        Security.addProvider(new BouncyCastleProvider()); // Necesario para BouncyCastle
    }

    // Cargar clave pública desde archivo PEM
    public static PublicKey loadPublicKey(InputStream is) throws Exception {
        try (PemReader reader = new PemReader(new InputStreamReader(is))) {
            PemObject pemObject = reader.readPemObject();
            byte[] content = pemObject.getContent();
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(content);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        }
    }

    // Cargar clave privada desde archivo PEM
    public static PrivateKey loadPrivateKey(InputStream is) throws Exception {
        try (PemReader reader = new PemReader(new InputStreamReader(is))) {
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

    public static String encryptHybrid(String plainText, PublicKey publicKey) throws Exception {
        // 1. Generar clave AES
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // AES-256
        SecretKey aesKey = keyGen.generateKey();

        // 2. IV
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // 3. Cifrar mensaje con AES
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
        byte[] encryptedMessage = aesCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // 4. Cifrar clave AES con RSA
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());

        // 5. Combinar y codificar
        return Base64.getEncoder().encodeToString(encryptedAesKey) + "::" +
                Base64.getEncoder().encodeToString(iv) + "::" +
                Base64.getEncoder().encodeToString(encryptedMessage);
    }

    public static String decryptHybrid(String encryptedData, PrivateKey privateKey) throws Exception {
        // 1. Separar partes
        String[] parts = encryptedData.split("::");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Formato inválido de datos cifrados.");
        }

        byte[] encryptedAesKey = Base64.getDecoder().decode(parts[0]);
        byte[] iv = Base64.getDecoder().decode(parts[1]);
        byte[] encryptedMessage = Base64.getDecoder().decode(parts[2]);

        // 2. Desencriptar clave AES con RSA
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        // 3. Desencriptar mensaje con AES
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
        byte[] decrypted = aesCipher.doFinal(encryptedMessage);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

}
