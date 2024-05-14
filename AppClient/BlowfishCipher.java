package AppClient;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class BlowfishCipher {
    private static final String ALGORITHM = "Blowfish";
    private Key secretKey;
    private String key;

    public BlowfishCipher(String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(), ALGORITHM);
        this.key = secret;
    }

    public String encrypt(String dataToEncrypt) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(dataToEncrypt.getBytes());
        System.out.println("msg encrypted with : " + key);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        System.out.println("msg decrypted with : " + key);
        return new String(decryptedData);
    }

}