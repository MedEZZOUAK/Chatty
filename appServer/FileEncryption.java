package appServer;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class FileEncryption {
    private static final String ALGORITHM = "Blowfish";


        public static byte[] encrypt(byte[] inputBytes , String key)
        {   int mode = Cipher.ENCRYPT_MODE;
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = null;
            try{
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(mode , secretKey);

            byte[] outputBytes = new byte[0];
            outputBytes = cipher.doFinal(inputBytes);
                System.out.println("data encrypted with key: " + key);
            return outputBytes;
            } catch ( NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                     BadPaddingException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }

        }

        public static byte[] decrypt(byte[] inputBytes , String key)
        {
            int mode = Cipher.DECRYPT_MODE;
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = null;
            try{
                cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(mode , secretKey);

                byte[] outputBytes = new byte[0];
                outputBytes = cipher.doFinal(inputBytes);
                System.out.println("data dycrepted with key : " + key);
                return outputBytes;
            } catch ( NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                      BadPaddingException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }

        }







}
