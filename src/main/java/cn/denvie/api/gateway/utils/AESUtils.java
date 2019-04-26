package cn.denvie.api.gateway.utils;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Provider;
import java.security.SecureRandom;

/**
 * AES加密工具。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class AESUtils {

    public static final String DEFAULT_CHARSET = "UTF-8";

    /***
     * AES加密算法加密（16进制编码）
     */
    public static String encrypt(String seed, String key) throws Exception {
        byte[] rawKey = key.getBytes(DEFAULT_CHARSET);
        byte[] result = encrypt(seed.getBytes(DEFAULT_CHARSET), rawKey);
        return toHex(result);
    }

    /***
     * AES加密算法加密（Base64编码）
     */
    public static String encryptToBase64(String seed, String key) throws Exception {
        byte[] rawKey = key.getBytes(DEFAULT_CHARSET);
        byte[] result = encrypt(seed.getBytes(DEFAULT_CHARSET), rawKey);
        return Base64Utils.encodeToString(result);
    }

    public static byte[] encryptByte(String seed, String key) throws Exception {
        byte[] rawKey = getRawKey(key.getBytes(DEFAULT_CHARSET));
        byte[] result = encrypt(seed.getBytes(DEFAULT_CHARSET), rawKey);
        return result;
    }

    /***
     * AES加密算法解密
     */
    public static String decryptString(byte[] byteData, byte[] byteKey) throws Exception {
        byte[] result = decrypt(byteData, byteKey);
        return new String(result, DEFAULT_CHARSET);
    }

    /***
     * AES加密算法解密
     */
    public static String decryptString(String content, String key) throws Exception {
        byte[] byteData = toByte(content);
        byte[] byteKey = key.getBytes(DEFAULT_CHARSET);
        byte[] result = decrypt(byteData, byteKey);
        return new String(result, DEFAULT_CHARSET);
    }

    /***
     * AES加密算法解密
     */
    public static String decryptStringFromBase64(String base64Content, String key) throws Exception {
        byte[] byteData = Base64Utils.decode(base64Content.getBytes(DEFAULT_CHARSET));
        byte[] byteKey = key.getBytes(DEFAULT_CHARSET);
        byte[] result = decrypt(byteData, byteKey);
        return new String(result, DEFAULT_CHARSET);
    }

    private static byte[] encrypt(byte[] byteData, byte[] byteKey) throws Exception {
        return Ase(byteData, byteKey, Cipher.ENCRYPT_MODE);
    }

    private static byte[] decrypt(byte[] byteData, byte[] byteKey) throws Exception {
        return Ase(byteData, byteKey, Cipher.DECRYPT_MODE);
    }

    private static byte[] Ase(byte[] byteData, byte[] byteKey, int opmode) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec skeySpec = new SecretKeySpec(byteKey, "AES");
        cipher.init(opmode, skeySpec);
        byte[] decrypted = cipher.doFinal(byteData);
        return decrypted;
    }

    public static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = null;
        /*int sdk_version = android.os.Build.VERSION.SDK_INT;
        if (sdk_version > 23) {  // Android  6.0 以上
            sr = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
        } else if (sdk_version >= 17) {
            sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        } else {
            sr = SecureRandom.getInstance("SHA1PRNG");
        }*/
        sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    public static String toHex(byte[] buf) {
        final String HEX = "0123456789ABCDEF";
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            result.append(HEX.charAt((buf[i] >> 4) & 0x0f)).append(
                    HEX.charAt(buf[i] & 0x0f));
        }
        return result.toString();
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }

    public static class CryptoProvider extends Provider {
        /**
         * Creates a Provider and puts parameters
         */
        public CryptoProvider() {
            super("Crypto", 1.0, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
            put("SecureRandom.SHA1PRNG",
                    "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
            put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
        }
    }

    public static void main(String[] args) throws Exception {
        String key = MD5Utils.md5("key", 16);
        String text = "abcdefg";
        String encrypt = encrypt(text, key);
        System.err.println(encrypt);
        System.err.println(decryptString(encrypt, key));

        String encrypt2 = encryptToBase64(text, key);
        System.err.println(encrypt2);
        System.err.println(decryptStringFromBase64(encrypt2, key));
    }

}