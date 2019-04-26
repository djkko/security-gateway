package cn.denvie.api.gateway.utils;

import org.springframework.util.Base64Utils;

import java.security.MessageDigest;

/**
 * MD5加密工具。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class MD5Utils {

    /**
     * 对字符串md5加密。
     *
     * @param text 传入要加密的字符串
     * @return MD5加密后的字符串
     */
    public static String md5(String text) {
        return md5(text, 32);
    }

    /**
     * 对字符串md5加密。
     *
     * @param text 需要加密的字符串
     * @param bit  加密的类型（16,32,64）
     * @return MD5加密后的字符串
     */
    public static String md5(String text, Integer bit) {
        String md5 = new String();
        try {
            // 创建加密对象
            MessageDigest md = MessageDigest.getInstance("md5");
            if (bit == 64) {
                byte[] digest = md.digest(text.getBytes("utf-8"));
                String base64 = Base64Utils.encodeToString(digest);
                md5 = base64;
            } else {
                // 计算MD5函数
                md.update(text.getBytes());
                byte b[] = md.digest();
                int i;
                StringBuilder sb = new StringBuilder();
                for (int offset = 0; offset < b.length; offset++) {
                    i = b[offset];
                    if (i < 0)
                        i += 256;
                    if (i < 16)
                        sb.append("0");
                    sb.append(Integer.toHexString(i));
                }
                md5 = sb.toString();
                if (bit == 16) {
                    // 截取32位md5为16位
                    String md16 = md5.substring(8, 24);
                    md5 = md16;
                    return md5;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static void main(String[] args) {
        String md5 = md5("password");
        String md52 = md5("password", 32);
        System.err.println(md5);
        System.err.println(md52);
    }
}
