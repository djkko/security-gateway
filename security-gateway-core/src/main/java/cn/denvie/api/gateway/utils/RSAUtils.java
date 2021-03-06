package cn.denvie.api.gateway.utils;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA非对称加解密工具。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class RSAUtils {

    private static final String ALGORITHM = "RSA";
    private static final String CHARSET = "UTF-8";

    public static final String KEY_PRIVATE = "private";
    public static final String KEY_PUBLIC = "public";

    /**
     * 生成RSA公私密钥对。
     * 私钥的key为{@link RSAUtils#KEY_PRIVATE private}，公钥的key为{@link RSAUtils#KEY_PUBLIC public}。
     *
     * @param keySize 密钥长度，512~65535，必需是64的整数位，默认1024
     * @return
     */
    public static Map<String, byte[]> generateRSAKey(int keySize) throws NoSuchAlgorithmException {
        Map<String, byte[]> map = new HashMap<>();
        // 初始化密钥
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(keySize);

        // 生成公私钥
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        byte[] rsaPrivateKeyEncoded = rsaPrivateKey.getEncoded();
        byte[] rsaPublicKeyEncoded = rsaPublicKey.getEncoded();

        map.put(KEY_PRIVATE, rsaPrivateKeyEncoded);
        map.put(KEY_PUBLIC, rsaPublicKeyEncoded);

        return map;
    }

    /**
     * 生成RSA公私密钥对。
     * 私钥的key为{@link RSAUtils#KEY_PRIVATE private}，公钥的key为{@link RSAUtils#KEY_PUBLIC public}。
     *
     * @param keySize 密钥长度，512~65535，必需是64的整数倍，默认1024
     * @return
     */
    public static Map<String, String> generateRSAKeyBase64(int keySize) throws NoSuchAlgorithmException {
        Map<String, String> stringMap = new HashMap<>();
        Map<String, byte[]> byteMap = generateRSAKey(keySize);
        if (byteMap != null && byteMap.size() == 2) {
            stringMap.put(KEY_PRIVATE, Base64Utils.encodeToString(byteMap.get(KEY_PRIVATE)));
            stringMap.put(KEY_PUBLIC, Base64Utils.encodeToString(byteMap.get(KEY_PUBLIC)));
        }
        return stringMap;
    }

    /**
     * 生成RSA公私密钥对。
     * 私钥的key为{@link RSAUtils#KEY_PRIVATE private}，公钥的key为{@link RSAUtils#KEY_PUBLIC public}。
     */
    public static Map<String, String> generateRSAKeyBase64() throws NoSuchAlgorithmException {
        return generateRSAKeyBase64(512);
    }

    /**
     * 公钥加密。
     *
     * @param rsaPublicKey 公钥byte[]
     * @param src          原文
     * @return byte[]密文
     */
    public static byte[] encryptByPublicKey(byte[] rsaPublicKey, String src) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(src.getBytes(CHARSET));
    }

    /**
     * 公钥加密。
     *
     * @param rsaPublicKeyBase64 Base64编码的公钥
     * @param src                原文
     * @return Base64编码密文
     */
    public static String encryptByPublicKey(String rsaPublicKeyBase64, String src) throws Exception {
        byte[] bytes = encryptByPublicKey(Base64Utils.decodeFromString(rsaPublicKeyBase64), src);
        if (bytes != null) {
            return Base64Utils.encodeToString(bytes);
        }
        return null;
    }

    /**
     * 私钥解密。
     *
     * @param rsaPrivateKey 私钥byte[]
     * @param src           密文
     * @return
     */
    public static byte[] decryptByPrivateKey(byte[] rsaPrivateKey, byte[] src) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(src);
    }

    /**
     * 私钥解密。
     *
     * @param rsaPrivateKeyBase64 Base64编码的私钥
     * @param srcBase64           Base64编码的密文
     * @return
     */
    public static String decryptByPrivateKey(String rsaPrivateKeyBase64, String srcBase64) throws Exception {
        byte[] bytes = decryptByPrivateKey(
                Base64Utils.decodeFromString(rsaPrivateKeyBase64), Base64Utils.decodeFromString(srcBase64));
        if (bytes != null) {
            try {
                return new String(bytes, CHARSET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 私钥加密。
     *
     * @param rsaPrivateKey 私钥byte[]
     * @param src           原文
     * @return byte[]密文
     */
    public static byte[] encryptByPrivateKey(byte[] rsaPrivateKey, String src) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(src.getBytes(CHARSET));
    }

    /**
     * 私钥加密。
     *
     * @param rsaPrivateKeyBase64 Base64编码的私钥
     * @param src                 原文
     * @return Base64编码密文
     */
    public static String encryptByPrivateKey(String rsaPrivateKeyBase64, String src) throws Exception {
        byte[] bytes = encryptByPrivateKey(Base64Utils.decodeFromString(rsaPrivateKeyBase64), src);
        if (bytes != null) {
            return Base64Utils.encodeToString(bytes);
        }
        return null;
    }

    /**
     * 公钥解密。
     *
     * @param rsaPublicKey 公钥byte[]
     * @param src          密文
     * @return
     */
    public static byte[] decryptByPublicKey(byte[] rsaPublicKey, byte[] src) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(src);
    }

    /**
     * 公钥解密。
     *
     * @param rsaPublicKeyBase64 Base64编码的公钥
     * @param srcBase64          Base64编码的密文
     * @return
     */
    public static String decryptByPublicKey(String rsaPublicKeyBase64, String srcBase64) throws Exception {
        byte[] bytes = decryptByPublicKey(
                Base64Utils.decodeFromString(rsaPublicKeyBase64), Base64Utils.decodeFromString(srcBase64));
        if (bytes != null) {
            try {
                return new String(bytes, CHARSET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 签名（MD5withRSA方式）。
     *
     * @param content       待签名内容
     * @param rsaPrivateKey 私钥
     * @return
     */
    public static byte[] sign(String content, byte[] rsaPrivateKey) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(content.getBytes(CHARSET));
        return signature.sign();
    }

    /**
     * 签名（MD5withRSA方式）。
     *
     * @param content             待签名内容
     * @param rsaPrivateKeyBase64 Base64编码的私钥
     * @return Base64编码的签名字符串
     */
    public static String sign(String content, String rsaPrivateKeyBase64) throws Exception {
        byte[] bytes = sign(content, Base64Utils.decodeFromString(rsaPrivateKeyBase64));
        if (bytes != null) {
            return Base64Utils.encodeToString(bytes);
        }
        return null;
    }

    /**
     * 签名验证（MD5withRSA方式）。
     *
     * @param src          原文
     * @param signBytes    已签名byte[]内容
     * @param rsaPublicKey 公钥
     * @return
     */
    public static boolean signValidate(String src, byte[] signBytes, byte[] rsaPublicKey) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicKey);
        signature.update(src.getBytes(CHARSET));
        return signature.verify(signBytes);
    }

    /**
     * 签名验证（MD5withRSA方式）。
     *
     * @param src                原文
     * @param signBase64         已签名Base64内容
     * @param rsaPublicKeyBase64 Base64公钥
     * @return
     */
    public static boolean signValidate(String src, String signBase64, String rsaPublicKeyBase64) throws Exception {
        return signValidate(src, Base64Utils.decodeFromString(signBase64),
                Base64Utils.decodeFromString(rsaPublicKeyBase64));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Test
    ///////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws Exception {

        String src = "rsa encrypt by denvie";

        // 公钥加密、私钥解密
        publicEncodePrivateDecode(src);

        // 私钥加密、公钥解密
        privateEncodePublicDecode(src);

        // 私钥签名、公钥验签
        privateSignPublicValidate(src);
    }

    private static void publicEncodePrivateDecode(String src) throws Exception {
        System.out.println("======== 公钥加密、私钥解密 ========");

        // 生成公私钥对
        Map<String, byte[]> keyMap = generateRSAKey(512);
        byte[] rsaPrivateKeyBytes = keyMap.get(KEY_PRIVATE);
        byte[] rsaPublicKeyBytes = keyMap.get(KEY_PUBLIC);
        System.out.println("RSAPrivateKey: " + Base64Utils.encodeToString(rsaPrivateKeyBytes));
        System.out.println("RSAPublicKey:  " + Base64Utils.encodeToString(rsaPublicKeyBytes));

        // 加密
        byte[] result = encryptByPublicKey(rsaPublicKeyBytes, src);
        System.out.println("公钥加密: " + Base64Utils.encodeToString(result));

        // 解密
        result = decryptByPrivateKey(rsaPrivateKeyBytes, result);
        System.out.println("私钥解密: " + new String(result, CHARSET));

    }

    private static void privateEncodePublicDecode(String src) throws Exception {
        System.out.println("======== 私钥加密、公钥解密 ========");

        // 生成公私钥对
        Map<String, byte[]> keyMap = generateRSAKey(512);
        byte[] rsaPrivateKeyBytes = keyMap.get(KEY_PRIVATE);
        byte[] rsaPublicKeyBytes = keyMap.get(KEY_PUBLIC);
        System.out.println("RSAPrivateKey: " + Base64Utils.encodeToString(rsaPrivateKeyBytes));
        System.out.println("RSAPublicKey:  " + Base64Utils.encodeToString(rsaPublicKeyBytes));

        // 加密
        byte[] result = encryptByPrivateKey(rsaPrivateKeyBytes, src);
        System.out.println("私钥加密: " + Base64Utils.encodeToString(result));

        // 解密
        result = decryptByPublicKey(rsaPublicKeyBytes, result);
        System.out.println("公钥解密: " + new String(result, CHARSET));
    }

    private static void privateSignPublicValidate(String src) throws Exception {
        System.out.println("======== 私钥签名、公钥验签 ========");

        // 生成公私钥对
        Map<String, byte[]> keyMap = generateRSAKey(512);
        byte[] rsaPrivateKeyBytes = keyMap.get(KEY_PRIVATE);
        byte[] rsaPublicKeyBytes = keyMap.get(KEY_PUBLIC);
        System.out.println("RSAPrivateKey: " + Base64Utils.encodeToString(rsaPrivateKeyBytes));
        System.out.println("RSAPublicKey:  " + Base64Utils.encodeToString(rsaPublicKeyBytes));

        // 签名
        byte[] result = sign(src, rsaPrivateKeyBytes);
        System.out.println("私钥签名: " + Base64Utils.encodeToString(result));

        // 验签
        System.out.println("公钥验签: " + signValidate(src, result, rsaPublicKeyBytes));
    }

}
