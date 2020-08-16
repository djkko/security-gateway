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
    public static byte[] generateAESKey(byte[] seedBytes) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(seedBytes);
        keyGenerator.init(128, secureRandom); // 192 and 256 bits may not be available
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] rawKey = secretKey.getEncoded();
        return rawKey;
    }

    /**
     * 根据种子生成AES密钥。
     */
    public static byte[] generateAESKey(String seed) throws Exception {
        return generateAESKey(seed.getBytes(CHARSET));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Test
    ///////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws Exception {

        String keySrc = "Denvie";
        String text = "AES Test By Denvie";

        // 16进制加解密
        byte[] keyBytes = generateAESKey(keySrc);
        System.out.println("生成HexKey: " + HexUtils.toHexString(keyBytes));
        String key = new String(keyBytes, CHARSET);
        String encrypt = encrypt(text, key);
        System.out.println("加密：" + encrypt);
        System.out.println("解密：" + decryptString(encrypt, key));

        // Base64加解密
        String randomKey = MD5Utils.md5(keySrc, 16);
        System.out.println("生成StringKey: " + randomKey);
        String encrypt2 = encryptToBase64(text, randomKey);
        System.out.println("加密：" + encrypt2);
        System.out.println("解密：" + decryptStringFromBase64(encrypt2, randomKey));
    }

}