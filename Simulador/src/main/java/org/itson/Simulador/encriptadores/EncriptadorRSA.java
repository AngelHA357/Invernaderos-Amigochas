/**
 * EncriptadorRSA.java
 */
package org.itson.Simulador.encriptadores;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

/**
 * @author Equipo1
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

    // Encriptar con clave pública
    public static byte[] encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    }

}
