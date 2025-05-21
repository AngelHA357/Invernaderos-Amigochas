package encriptadores;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

public class EncriptadorRSA {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static PublicKey loadPublicKey(String filepath) throws Exception {
        try (PemReader reader = new PemReader(new FileReader(filepath))) {
            PemObject pemObject = reader.readPemObject();
            byte[] content = pemObject.getContent();
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(content);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        }
    }

    public static PrivateKey loadPrivateKey(String filepath) throws Exception {
        try (PemReader reader = new PemReader(new FileReader(filepath))) {
            PemObject pemObject = reader.readPemObject();
            byte[] content = pemObject.getContent();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        }
    }

    public static byte[] encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    }

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
        return Base64.getEncoder().encodeToString(encryptedAesKey) + "::"
                + Base64.getEncoder().encodeToString(iv) + "::"
                + Base64.getEncoder().encodeToString(encryptedMessage);
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
