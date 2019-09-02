package com.fqserver.core.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fqserver.core.compress.ZLibUtils;


/**
 * 基于 httpclient 4.3.6版本的 http工具类
 * 
 * @author mcSui
 * 
 */
public class HttpToolkit {

    private static Logger log = LoggerFactory.getLogger(HttpToolkit.class);

    public static final String CHARSET = "utf-8";

    private static HttpParams httpParams;
    private static PoolingClientConnectionManager cm;
    // private static final HttpClient httpClient;

    public static final int DEFAULT_CONN_TIMEOUT = 5000;
    public static final int DEFAULT_SO_TIMEOUT = 15000;

    static {
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
        cm.setMaxTotal(1024);
        cm.setDefaultMaxPerRoute(1024);

        httpParams = new SyncBasicHttpParams();
        httpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
        httpParams.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        httpParams.setIntParameter(CoreConnectionPNames.SO_LINGER, 0);

    }

    public static String doGet(String url, Map<String, String> params) {
        return doGet(url, params, CHARSET);
    }

    /**
     * HTTP Get 获取内容
     * 
     * @param url
     *            请求的url地址 ?之前的地址
     * @param params
     *            请求的参数
     * @param charset
     *            编码格式
     * @return 页面内容
     */
    public static String doGet(String url, Map<String, String> params, String charset) {
        return doGet(url, params, charset, DEFAULT_CONN_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    public static String doGet(String url,
                               Map<String, String> params,
                               final int connectTimeout,
                               final int soTimeout) {
        return doGet(url, params, CHARSET, connectTimeout, soTimeout);
    }

    /**
     * doGet
     * 
     * @param url
     * @param params
     * @param charset
     * @param connectTimeout
     * @param soTimeout
     * @return
     */
    public static String doGet(String url,
                               Map<String, String> params,
                               String charset,
                               final int connectTimeout,
                               final int soTimeout) {

        try {
            String param = "";
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                param = EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            return doGet(url, param, charset, null, connectTimeout, soTimeout);
        }
        catch (Exception e) {
            // e.printStackTrace();
            log.error(url + " request error doGet: ", e);
        }
        return null;

    }

    public static String doGet(String url, String param) {
        return doGet(url, param, CHARSET, null, DEFAULT_CONN_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    public static String doGet(String url,
                               String param,
                               final int connectTimeout,
                               final int soTimeout) {
        return doGet(url, param, CHARSET, null, connectTimeout, soTimeout);
    }

    public static String doGet(String url, String param, Header[] headers) {
        return doGet(url, param, CHARSET, headers, DEFAULT_CONN_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    public static String doGet(String url,
                               String param,
                               Header[] headers,
                               final int connectTimeout,
                               final int soTimeout) {
        return doGet(url, param, CHARSET, headers, connectTimeout, soTimeout);
    }

    public static String doGet(String url,
                               String param,
                               String charset,
                               Header[] headers,
                               final int connectTimeout,
                               final int soTimeout) {

        // log.debug(url + " , params :" + param);

        long time1 = System.currentTimeMillis();

        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            if (param != null && param.length() > 0) {
                url += "?" + param;
            }

            HttpGet httpGet = new HttpGet(url);

            byte[] b = doRequest(httpGet, headers, connectTimeout, soTimeout, null);

            String result = null;

            if (b != null) {

                result = new String(b, charset);

            }

            // long time2 = System.currentTimeMillis();
            // log.debug(url + " time comsuming:" + (time2 - time1) + "ms");
            // log.debug("response str:" + result);
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error(url + " request error doGet: ", e);
        }
        long time3 = System.currentTimeMillis();
        log.debug(url + " time comsuming:" + (time3 - time1) + "ms");
        log.debug("response null");
        return null;
    }

    public static String doPost(String url, String param, boolean needCompress) {
        return doPost(url, param, CHARSET, needCompress, DEFAULT_CONN_TIMEOUT, DEFAULT_SO_TIMEOUT);
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
    public static String doPost(String url, String param) {

        log.debug(url + " , params :" + param);

        return doPost(url, param, CHARSET, false, DEFAULT_CONN_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    public static String doPost(String url, String param, int connectTimeout, int soTimeout) {

        log.debug(url + " , params :" + param);

        return doPost(url, param, CHARSET, false, connectTimeout, soTimeout);
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
    public static String doPost(String url,
                                String param,
                                String CharsetName,
                                boolean needCompress,
                                final int connectTimeout,
                                final int soTimeout) {
        try {
            return doPost(url,
                          param == null ? null : param.getBytes(CharsetName),
                          CharsetName,
                          needCompress,
                          connectTimeout,
                          soTimeout);
        }
        catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
            log.error(url + " request error doPost: ", e);
        }
        return null;
    }

    /**
     * 发送 application/json
     * 
     * @param url
     * @param params
     * @return
     */
    public static String doJsonPost(String url, String params) {
        return doJsonPost(url, params, null);
    }

    public static String doJsonPost(String url, String params, String clintIP) {
        return doJsonPost(url, params, clintIP, DEFAULT_CONN_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    public static String doJsonPost(String url,
                                    String params,
                                    String clintIP,
                                    final int connectTimeout,
                                    final int soTimeout) {

        log.debug(url + " , params :" + params);

        byte[] param = null;
        try {
            param = params == null ? null : params.getBytes("utf-8");
        }

        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        boolean needCompress = false;

        long time1 = System.currentTimeMillis();

        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            HttpPost httpPost = new HttpPost(url);

            List<Header> lstHeaders = new ArrayList<Header>();
            lstHeaders.add(new BasicHeader("Content-Type", "application/json"));
            if (clintIP != null && clintIP.trim().length() > 0) {
                lstHeaders.add(new BasicHeader("x-forwarded-for", "X-Forwarded-For:" + clintIP));
            }

            if (param != null) {
                if (needCompress) {
                    param = ZLibUtils.compress(param);
                }
            }

            byte[] byteRes = doPost(url,
                                    param,
                                    lstHeaders.toArray(new Header[]{}),
                                    connectTimeout,
                                    soTimeout);

            if (byteRes != null) {

                if (needCompress) {
                    byteRes = needDecompress(byteRes, needCompress);
                }
                // response.close();
                long time2 = System.currentTimeMillis();
                // log.debug(url + " time comsuming:" + (time2 - time1) + "ms");
                // log.debug("response str:" + new String(byteRes, "utf-8"));
                return new String(byteRes, "utf-8");
            }
        }
        catch (Exception e) {
            // e.printStackTrace();
            log.error(url + " request error doJsonPost: ", e);
        }
        long time3 = System.currentTimeMillis();
        log.debug(url + " time comsuming:" + (time3 - time1) + "ms");
        log.debug("response null");
        return null;

    }

    /**
     * HTTP Post 获取内容
     * 
     * @param url
     *            请求的url地址 ?之前的地址
     * @param params
     *            请求的参数
     * @param charset
     *            编码格式
     * @return 页面内容
     */
    public static String doPost(String url,
                                byte[] param,
                                String charset,
                                boolean needCompress,
                                final int connectTimeout,
                                final int soTimeout) {

        long time1 = System.currentTimeMillis();

        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {

            if (param != null) {
                if (needCompress) {
                    param = ZLibUtils.compress(param);
                }
            }

            byte[] byteRes = doPost(url, param, null, connectTimeout, soTimeout);

            if (byteRes != null) {

                if (needCompress) {
                    byteRes = needDecompress(byteRes, needCompress);
                }

                long time2 = System.currentTimeMillis();
                log.debug(url + " 2-1 time comsuming:" + (time2 - time1) + "ms");
                log.debug("response str:" + new String(byteRes, charset));
                return new String(byteRes, charset);
            }
        }
        catch (Exception e) {
            long time2 = System.currentTimeMillis();
            log.debug(url + " Exceptioned time comsuming:" + (time2 - time1) + "ms");
            // e.printStackTrace();
            log.error(url + " request error doPost: ", e);
        }
        long time3 = System.currentTimeMillis();
        log.debug(url + " 3-1 time comsuming:" + (time3 - time1) + "ms");
        log.debug("response null");
        return null;
    }

    /**
     * HTTP Post 获取内容
     * 
     * @param url
     *            请求的url地址 ?之前的地址
     * @param params
     *            请求的参数
     * @param charset
     *            编码格式
     * @return 页面内容
     */
    public static byte[] doPost(String url,
                                byte[] param,
                                final Header[] headers,
                                final int connectTimeout,
                                final int soTimeout) {

        if (StringUtils.isBlank(url)) {
            return null;
        }
        HttpPost httpPost = new HttpPost(url);

        httpPost.setEntity(new ByteArrayEntity(param));

        byte[] b = doRequest(httpPost, headers, connectTimeout, soTimeout, null);
        return b;

    }

    public static final byte[] doRequest(final HttpRequestBase req,
                                         final Header[] headers,
                                         final int connectTimeout,
                                         final int soTimeout,
                                         List<Header> responseHeaders) {
        byte[] result = null;

        try {
            if (headers != null && headers.length > 0) {
                for (Header h : headers) {
                    req.setHeader(h);
                }
            }

            HttpParams p = httpParams.copy();
            p.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
            p.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeout);

            CloseableHttpClient httpClient = new DefaultHttpClient(cm, p);
            try {
                CloseableHttpResponse response = httpClient.execute(req);
                try {
                    if (response == null) {
                        return result;
                    }
                    if (responseHeaders != null) {
                        for (Header h : response.getAllHeaders()) {
                            responseHeaders.add(h);
                        }
                    }

                    HttpEntity entity = response.getEntity();
                    try {
                        if (entity != null) {
                            result = EntityUtils.toByteArray(entity);
                        }
                    }
                    finally {
                        EntityUtils.consume(entity);
                    }
                }
                catch (Exception e) {
                    log.error(String.format("[HttpUtils %s]response error, url:%s",
                                            req.getClass().getSimpleName(),
                                            req.getURI()), e);
                    return result;
                }
                finally {
                    if (response != null) {
                        response.close();
                    }
                }
            }
            finally {
                if (httpClient != null) {
                    httpClient.close();
                }
            }
        }
        catch (ClientProtocolException e) {
            log.error(String.format("[HttpUtils %s]ClientProtocolException url:%s,  error: ",
                                    req.getClass().getSimpleName(),
                                    req.getURI()), e);
        }
        catch (SocketTimeoutException e) {
            log.error(String.format("[HttpUtils %s]socket timout url:%s,  error: ",
                                    req.getClass().getSimpleName(),
                                    req.getURI()), e);
        }
        catch (IOException e) {
            log.error(String.format("[HttpUtils %s]IOException url:%s,  error: ",
                                    req.getClass().getSimpleName(),
                                    req.getURI()), e);
        }
        return result;
    }

    // public static final CloseableHttpResponse doRequest(
    // final HttpRequestBase req, final int connectTimeout,
    // final int soTimeout) {
    //
    // try {
    //
    // HttpParams p = httpParams.copy();
    // p.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
    // connectTimeout);
    // p.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeout);
    //
    // CloseableHttpClient httpClient = new DefaultHttpClient(cm, p);
    //
    // try {
    // return httpClient.execute(req);
    // } finally {
    // if (httpClient != null) {
    // httpClient.close();
    // }
    // }
    // } catch (ClientProtocolException e) {
    // log.error(String.format(
    // "[HttpUtils %s]ClientProtocolException url:%s,  error: ",
    // req.getClass().getSimpleName(), req.getURI()), e);
    // } catch (SocketTimeoutException e) {
    // log.error(String.format(
    // "[HttpUtils %s]socket timout url:%s,  error: ", req
    // .getClass().getSimpleName(), req.getURI()), e);
    // } catch (IOException e) {
    // log.error(String.format(
    // "[HttpUtils %s]IOException url:%s,  error: ", req
    // .getClass().getSimpleName(), req.getURI()), e);
    // }
    // return null;
    // }

    // public static void doAsyncPost(String url, String param,
    // FutureCallback<HttpResponse> callback) {
    // doAsyncPost(url, param, CHARSET, false, callback);
    // }

    // public static void doAsyncPost(String url, String param,
    // String CharsetName, boolean needCompress,
    // FutureCallback<HttpResponse> callback) {
    // try {
    // doAsyncPost(url, param.getBytes(CharsetName), CharsetName,
    // needCompress, callback);
    // } catch (UnsupportedEncodingException e) {
    // e.printStackTrace();
    // }
    // return;
    // }

    // public static void doAsyncPost(String url, byte[] param, String charset,
    // boolean needCompress, FutureCallback<HttpResponse> callback) {
    // if (StringUtils.isBlank(url)) {
    // return;
    // }
    // try {
    //
    // RequestConfig requestConfig = RequestConfig.custom()
    // .setSocketTimeout(3000).setConnectTimeout(3000).build();
    // CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
    // .setDefaultRequestConfig(requestConfig).build();
    // try {
    // httpclient.start();
    //
    // final HttpPost request = new HttpPost(url);
    // if (needCompress) {
    // param = ZLibUtils.compress(param);
    // }
    // request.setEntity(new ByteArrayEntity(param));
    //
    // final CountDownLatch latch = new CountDownLatch(1);
    //
    // httpclient.execute(request, callback);
    //
    // latch.await();
    // System.out.println("Shutting down");
    //
    // } finally {
    // httpclient.close();
    // }
    // System.out.println("Done");
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    public static byte[] needDecompress(byte[] in, boolean needDecompress) throws IOException,
            DataFormatException {

        if (needDecompress) {
            return ZLibUtils.decompress(in);
        }

        return in;
    }

    public static byte[] needDecompress(byte[] in) throws IOException, DataFormatException {
        return needDecompress(in, true);
    }

}