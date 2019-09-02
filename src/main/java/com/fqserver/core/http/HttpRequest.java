package com.fqserver.core.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fqserver.core.compress.GZipUtils;
import com.fqserver.core.compress.ZLibUtils;



public class HttpRequest {
    public static final int DEFAULT_CONN_TIMEOUT = 5000;
    public static final int DEFAULT_SO_TIMEOUT = 30000;

    public static String sendGet(String urlNameString) {
        BufferedInputStream in = null;
        byte[] byteRes = {};
        try {

            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setConnectTimeout(DEFAULT_CONN_TIMEOUT);
            connection.setReadTimeout(DEFAULT_SO_TIMEOUT);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                                          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            // Map<String, List<String>> map = connection.getHeaderFields();
            // // 遍历所有的响应头字段
            // for (String key : map.keySet()) {
            // System.out.println(key + "--->" + map.get(key));
            // }
            //
            in = new BufferedInputStream(connection.getInputStream());
            byteRes = needDecompress(in, false);

        }
        catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            return new String(byteRes, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String sendGet(String urlNameString, int timeout) {
        BufferedInputStream in = null;
        byte[] byteRes = {};
        try {

            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(DEFAULT_SO_TIMEOUT);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                                          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            // Map<String, List<String>> map = connection.getHeaderFields();
            // // 遍历所有的响应头字段
            // for (String key : map.keySet()) {
            // System.out.println(key + "--->" + map.get(key));
            // }
            //
            in = new BufferedInputStream(connection.getInputStream());
            byteRes = needDecompress(in, false);

        }
        catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            return new String(byteRes, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        BufferedInputStream in = null;
        byte[] byteRes = {};
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setConnectTimeout(DEFAULT_CONN_TIMEOUT);
            connection.setReadTimeout(DEFAULT_SO_TIMEOUT);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent",
                                          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // // 获取所有响应头字段
            // Map<String, List<String>> map = connection.getHeaderFields();
            // // 遍历所有的响应头字段
            // for (String key : map.keySet()) {
            // System.out.println(key + "--->" + map.get(key));
            // }
            //
            in = new BufferedInputStream(connection.getInputStream());
            byteRes = needDecompress(in, false);

        }
        catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            return new String(byteRes, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String sendPropGet(String url, String param, String typeName) {
        BufferedInputStream in = null;
        byte[] byteRes = {};
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("Content-Type", typeName);
            connection.setConnectTimeout(DEFAULT_CONN_TIMEOUT);
            connection.setReadTimeout(DEFAULT_SO_TIMEOUT);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent",
                                          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // // 获取所有响应头字段
            // Map<String, List<String>> map = connection.getHeaderFields();
            // // 遍历所有的响应头字段
            // for (String key : map.keySet()) {
            // System.out.println(key + "--->" + map.get(key));
            // }
            //
            in = new BufferedInputStream(connection.getInputStream());
            byteRes = needDecompress(in, false);

        }
        catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            return new String(byteRes, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String sendJsonGet(String url) {
        return sendJsonGet(url, null);
    }

    public static String sendJsonGet(String url, String param) {
        BufferedInputStream in = null;
        byte[] byteRes = {};
        try {
            String urlNameString = url;
            if (param != null && param.trim().length() > 0) {
                urlNameString = url + "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(DEFAULT_CONN_TIMEOUT);
            connection.setReadTimeout(DEFAULT_SO_TIMEOUT);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent",
                                          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // // 获取所有响应头字段
            // Map<String, List<String>> map = connection.getHeaderFields();
            // // 遍历所有的响应头字段
            // for (String key : map.keySet()) {
            // System.out.println(key + "--->" + map.get(key));
            // }
            //
            in = new BufferedInputStream(connection.getInputStream());
            byteRes = needDecompress(in, false);

        }
        catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            return new String(byteRes, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String sendGet(String url, String param, String CharsetName) {
        BufferedInputStream in = null;
        byte[] byteRes = {};
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setConnectTimeout(DEFAULT_CONN_TIMEOUT);
            connection.setReadTimeout(DEFAULT_SO_TIMEOUT);

            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                                          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            // Map<String, List<String>> map = connection.getHeaderFields();
            // // 遍历所有的响应头字段
            // for (String key : map.keySet()) {
            // System.out.println(key + "--->" + map.get(key));
            // }
            //
            in = new BufferedInputStream(connection.getInputStream());
            byteRes = needDecompress(in, false);

        }
        catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            return new String(byteRes, CharsetName);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String sendGet(String url, String param, int timeOut) {
        return sendGet(url, param, "utf-8", timeOut);
    }

    public static String sendGet(String url, String param, String CharsetName, int timeOut) {
        BufferedInputStream in = null;
        byte[] byteRes = {};
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setConnectTimeout(timeOut);
            connection.setReadTimeout(timeOut);

            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                                          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            // Map<String, List<String>> map = connection.getHeaderFields();
            // // 遍历所有的响应头字段
            // for (String key : map.keySet()) {
            // System.out.println(key + "--->" + map.get(key));
            // }
            //
            in = new BufferedInputStream(connection.getInputStream());
            byteRes = needDecompress(in, false);

        }
        catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            return new String(byteRes, CharsetName);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String sendPost(String url, String param, boolean needCompress) {
        return sendPost(url, param, needCompress, needCompress);
    }

    public static String sendPost(String url,
                                  String param,
                                  boolean needCompress,
                                  boolean needDecompress) {
        return sendPost(url, param, "utf-8", needCompress, needDecompress);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     * 
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        return sendPost(url, param, "utf-8", false, false);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     * 
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url,
                                  String param,
                                  String CharsetName,
                                  boolean needCompress,
                                  boolean needDecompress) {
        try {
            return sendPost(url, param.getBytes(CharsetName), needCompress, needDecompress);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     * 
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url,
                                  byte[] param,
                                  boolean needCompress,
                                  boolean needDecompress) {
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        byte[] byteRes = new byte[1024];
        try {

            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setConnectTimeout(DEFAULT_CONN_TIMEOUT);
            conn.setReadTimeout(DEFAULT_SO_TIMEOUT);
            conn.setRequestProperty("Accept-Encoding", "*");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new BufferedOutputStream(conn.getOutputStream());
            // 发送请求参数

            if (needCompress) {
                param = ZLibUtils.compress(param);
            }

            if (param != null) {
                out.write(param);
            }
            // flush输出流的缓冲
            out.flush();
            // 定义输入流来读取URL的响应
            in = new BufferedInputStream(conn.getInputStream());
            byteRes = needDecompress(in, needDecompress);

        }
        catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }

                if (in != null) {
                    in.close();
                }

            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            return new String(byteRes, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String sendJsonPost(String url, String param) {
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        byte[] byteRes = new byte[1024];
        try {

            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setConnectTimeout(DEFAULT_CONN_TIMEOUT);
            conn.setReadTimeout(DEFAULT_SO_TIMEOUT);

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new BufferedOutputStream(conn.getOutputStream());
            // 发送请求参数

            if (param != null) {
                out.write(param.getBytes("utf-8"));
            }
            // flush输出流的缓冲
            out.flush();
            // 定义输入流来读取URL的响应
            in = new BufferedInputStream(conn.getInputStream());
            byteRes = needDecompress(in, false);

        }
        catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }

                if (in != null) {
                    in.close();
                }

            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            return new String(byteRes, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] needDecompress(InputStream in, boolean needDecompress) throws Exception {

        int count = 0;
        // long curTime = System.currentTimeMillis();
        while (count == 0) {
            count = in.available();
            // if (System.currentTimeMillis() - curTime == 15000) {
            // count = 1024;
            // break;
            // }
        }

        if (needDecompress) {
            return ZLibUtils.decompress(in);
        }

        return getBytesFromInputStream(in);
    }

    public static byte[] getBytesFromInputStream(final InputStream in) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(in.available());
        final byte[] buf = new byte[4096];
        int n = -1;
        while ((n = in.read(buf, 0, 4096)) != -1) {
            bos.write(buf, 0, n);
        }
        return bos.toByteArray();
    }

    public static String showPost(String httpUrl, String params) throws Exception {
        String resultData = "";
        URL url = null;
        url = new URL(httpUrl);
        if (url != null) {
            // 使用HttpURLConnection打开连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 因为要求使用Post方式提交数据，需要设置为true
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            urlConn.setReadTimeout(5000);
            urlConn.setRequestMethod("POST");
            // Post 请求不能使用缓存
            urlConn.setUseCaches(false);
            urlConn.setInstanceFollowRedirects(true);
            // 配置本次连接的Content-Type，配置为application/x-www-form-urlencoded
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // urlConn.setRequestProperty("Content-Type", "text/html");//流
            // 要注意的事connection.getOutputStream会隐含地进行connect。
            urlConn.connect();
            OutputStream out = urlConn.getOutputStream();

            // 将要上传的内容写入流中
            out.write(params.getBytes("utf-8"));
            // 刷新，关闭
            out.flush();
            out.close();
            // 得到读取的数据
            InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
            BufferedReader buffer = new BufferedReader(in);
            String str = null;
            while ((str = buffer.readLine()) != null) {
                resultData += str + "\n";
            }
            in.close();
            urlConn.disconnect();
        }
        return resultData;
    }

    public static byte[] needDecompress(InputStream in) throws Exception {
        return needDecompress(in, true);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param content
     * 
     * @return 所代表远程资源的响应结果
     */
    public static final byte[] doRequest(final String url,
                                         final byte[] content,
                                         final BaseHeader[] headers,
                                         final int connectTimeout,
                                         final int soTimeout,
                                         List<BaseHeader> responseHeaders) {
        BufferedInputStream in = null;

        HttpURLConnection conn = null;
        try {

            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();

            // 设置通用的请求属性
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(soTimeout);
            conn.setRequestProperty("Accept-Encoding", "*");
            conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            if (headers != null && headers.length > 0) {
                for (BaseHeader h : headers) {
                    conn.setRequestProperty(h.getName(), h.getValue());
                }
            }

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            // conn.setInstanceFollowRedirects(false);

            conn.connect();

            // 发送请求参数
            if (content != null) {
                // 获取URLConnection对象对应的输出流
                BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());

                out.write(content);

                // flush输出流的缓冲
                out.flush();
                out.close();
            }

            // 定义输入流来读取URL的响应
            in = new BufferedInputStream(conn.getInputStream());

            String zipType = null;

            if (responseHeaders != null) {
                Map<String, List<String>> map = conn.getHeaderFields();
                for (Entry<String, List<String>> h : map.entrySet()) {
                    List<String> v = h.getValue();
                    responseHeaders.add(new BaseHeader(h.getKey(), v.size() > 0 ? v.get(0) : ""));
                    if ("Content-Encoding".equalsIgnoreCase(h.getKey()) && v.size() > 0) {
                        zipType = v.get(0);
                    }
                }
            }
            byte[] b = getBytesFromInputStream(in);
            if (zipType != null) {
                if (zipType.equalsIgnoreCase("gzip")) {
                    b = GZipUtils.decompress(b);
                } else if (zipType.equalsIgnoreCase("deflate")) {
                    b = ZLibUtils.decompress(b);
                }
            }
            return b;

        }
        catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }

                if (conn != null) {
                    conn.disconnect();
                }

            }
            catch (IOException e) {
                System.out.println("关闭清理 POST 请求出现异常！" + e);
            }
        }
        return null;
    }

    public static class BaseHeader implements Serializable {

        private static final long serialVersionUID = -5427236326487562175L;

        private final String name;
        private final String value;

        /**
         * Constructor with name and value
         *
         * @param name
         *            the header name
         * @param value
         *            the header value
         */
        public BaseHeader(final String name, final int value) {
            this(name, Integer.toString(value));
        }

        public BaseHeader(final String name, final String value) {
            this.name = name == null ? "Name" : name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return String.format("%s: %s", this.getName(), this.getValue());
        }
    }
}