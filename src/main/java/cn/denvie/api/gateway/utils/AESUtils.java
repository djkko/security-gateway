package cn.denvie.api.gateway.utils;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * AES对称加解密工具。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class AESUtils {

    private static final String ALGORITHM = "AES";
    private static final String CHARSET = "UTF-8";

    /**
     * AES加密算法加密（结果为16进制编码）
     */
    public static String encrypt(String seed, String key) throws Exception {
        byte[] rawData = seed.getBytes(CHARSET);
        byte[] rawKey = key.getBytes(CHARSET);
        byte[] result = encrypt(rawData, rawKey);
        return HexUtils.toHexString(result);
    }

    /**
     * AES加密算法加密（结果为Base64编码）
     */
    public static String encryptToBase64(String seed, String key) throws Exception {
        byte[] rawKey = key.getBytes(CHARSET);
        byte[] result = encrypt(seed.getBytes(CHARSET), rawKey);
        return Base64Utils.encodeToString(result);
    }

    /**
     * AES解密（解密16进制字符串）
     */
    public static String decryptString(String content, String key) throws Exception {
        byte[] byteData = HexUtils.fromHexString(content);
        byte[] byteKey = key.getBytes(CHARSET);
        byte[] result = decrypt(byteData, byteKey);
        return new String(result, CHARSET);
    }

    /***
     * AES解密（解密Base64字符串）
     */
    public static String decryptStringFromBase64(String base64Content, String key) throws Exception {
        byte[] byteData = Base64Utils.decode(base64Content.getBytes(CHARSET));
        byte[] byteKey = key.getBytes(CHARSET);
        byte[] result = decrypt(byteData, byteKey);
        return new String(result, CHARSET);
    }

    private static byte[] encrypt(byte[] byteData, byte[] byteKey) throws Exception {
        return doFinal(byteData, byteKey, Cipher.ENCRYPT_MODE);
    }

    private static byte[] decrypt(byte[] byteData, byte[] byteKey) throws Exception {
        return doFinal(byteData, byteKey, Cipher.DECRYPT_MODE);
    }

    private static byte[] doFinal(byte[] byteData, byte[] byteKey, int opmode) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec skeySpec = new SecretKeySpec(byteKey, ALGORITHM);
        cipher.init(opmode, skeySpec);
        byte[] decrypted = cipher.doFinal(byteData);
        return decrypted;
    }

    /**
     * 根据种子生成AES密钥。
     */
    public static byte[] generateAESKey(String seed) throws Exception {
        return generateAESKey(seed.getBytes(CHARSET));
    }

    /**
     * 根据种子生成AES密钥。
     */
    public static byte[] generateAESKey(byte[] seedBytes) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seedBytes);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Test
    ///////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws Exception {

        String key = MD5Utils.md5("Denvie", 16);
        String text = "待加密的内容";

        // 16进制加解密
        String encrypt = encrypt(text, key);
        System.err.println(encrypt);
        System.err.println(decryptString(encrypt, key));

        // Base64加解密
        String encrypt2 = encryptToBase64(text, key);
        System.err.println(encrypt2);
        System.err.println(decryptStringFromBase64(encrypt2, key));
    }

}