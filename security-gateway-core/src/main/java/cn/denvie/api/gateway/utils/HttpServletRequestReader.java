package cn.denvie.api.gateway.utils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * HttpServletRequestReader。
 *
 * HttpServletRequest请求中的body内容仅能调用request.getInputStream()，
 * request.getReader()和request.getParameter("key")方法读取一次，重复读取会报java.io.IOException: Stream closed 异常。
 *
 * @author DengZhaoyong
 * @version 1.2.1
 */
public class HttpServletRequestReader {

    // 字符串读取
    public static String readAsString(HttpServletRequest request) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = request.getReader();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    // 二进制读取
    public static byte[] readAsBytes(HttpServletRequest request) {
        int len = request.getContentLength();
        byte[] buffer = new byte[len];
        ServletInputStream in = null;
        try {
            in = request.getInputStream();
            in.read(buffer, 0, len);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer;
    }

}
