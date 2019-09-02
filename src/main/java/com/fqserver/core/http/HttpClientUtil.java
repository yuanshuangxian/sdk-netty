package com.fqserver.core.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fqserver.core.json.JsonUtil;
import com.fqserver.lang.util.Encoding;


public class HttpClientUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final String ISO_8859_1 = Encoding.ISO_8859_1.name(); // "latin-1";
    public static final String DEFAULT_CHARSET = Encoding.UTF_8.name();

    private static final PoolingHttpClientConnectionManager connManager;
    // private static final CloseableHttpClient httpClient;
    private static final RequestConfig defaultRequestConfig;

    public static Header[] Default_Headers = null;

    public static final int DEF_MaxHeaderCount = 200;
    public static final int DEF_MaxLineLength = 2000;

    public static final int DEF_ConnectTimeToLive = 300;

    public static final int DEF_ConnectMaxTotal = 1024;
    public static final int DEF_ConnectMaxPerRoute = 1024;

    public static final int DEF_ConnectBufferSize = 8 * 1024;

    public static final int DEFAULT_CONN_TIMEOUT = 5000;
    public static final int DEFAULT_SO_TIMEOUT = 5000;

    static {

        connManager = new PoolingHttpClientConnectionManager();

        // Create socket configuration
        SocketConfig socketConfig = SocketConfig.custom()
                                                .setSoTimeout(DEFAULT_SO_TIMEOUT)
                                                .setSoReuseAddress(true)
                                                .setSoLinger(-1)
                                                .setSoKeepAlive(true)
                                                .setTcpNoDelay(false)
                                                .setBacklogSize(0)
                                                .setRcvBufSize(0)
                                                .setSndBufSize(0)
                                                .build();
        connManager.setDefaultSocketConfig(socketConfig);

        // // Create message constraints
        MessageConstraints messageConstraints = MessageConstraints.custom()
                                                                  .setMaxHeaderCount(DEF_MaxHeaderCount)
                                                                  .setMaxLineLength(DEF_MaxLineLength)
                                                                  .build();

        // Create connection configuration
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                                                            .setMalformedInputAction(CodingErrorAction.IGNORE)
                                                            .setUnmappableInputAction(CodingErrorAction.IGNORE)
                                                            .setCharset(Consts.UTF_8)
                                                            .setBufferSize(DEF_ConnectBufferSize)
                                                            .setMessageConstraints(messageConstraints)
                                                            .build();

        connManager.setDefaultConnectionConfig(connectionConfig);
        // 设置连接池最大并发连接
        connManager.setMaxTotal(DEF_ConnectMaxTotal);
        // 设置单个路由最大连接，覆盖默认值2
        connManager.setDefaultMaxPerRoute(DEF_ConnectMaxPerRoute);
        connManager.setValidateAfterInactivity(2000);

        // requestConfig =
        // RequestConfig.custom().setSocketTimeout(connectTimeout)
        // .setConnectTimeout(connectTimeout)
        // .setConnectionRequestTimeout(connectTimeout).build();

        // 设置全局请求参数
        defaultRequestConfig = RequestConfig.custom().setExpectContinueEnabled(false)
        // .setContentCompressionEnabled(true)
                                            .setAuthenticationEnabled(true)
                                            // .setCookieSpec(CookieSpecs.DEFAULT)
                                            // .setTargetPreferredAuthSchemes(
                                            // Arrays.asList(AuthSchemes.NTLM,
                                            // AuthSchemes.DIGEST))
                                            // .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                                            .setRedirectsEnabled(true)
                                            .setMaxRedirects(50)
                                            .setRelativeRedirectsAllowed(true)
                                            .setConnectTimeout(DEFAULT_CONN_TIMEOUT)
                                            .setSocketTimeout(DEFAULT_SO_TIMEOUT)
                                            .setConnectionRequestTimeout(DEFAULT_CONN_TIMEOUT)
                                            .build();

        // 初始化httpClient
        // httpClient = getHttpClient();

        // DEF_Headers = ArraysUtil.asArray(new BasicHeader("Connection",
        // "Keep-Alive"));
    }

    public static CloseableHttpClient getHttpClient() {
        // 初始化httpClient
        // return HttpClients.createDefault();
        return HttpClients.custom()
                          .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                          .setConnectionTimeToLive(DEF_ConnectTimeToLive, TimeUnit.SECONDS)
                          // 必须设置setConnectionManagerShared为true, 连接池复用
                          .setConnectionManagerShared(true)
                          .setConnectionManager(connManager)

                          .setRetryHandler(new DefaultHttpRequestRetryHandler(3, false))
                          .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
                          .setDefaultRequestConfig(defaultRequestConfig)
                          .build();
    }

    public static String doGet(final String url, final String params) {
        return doGet(url, params, DEFAULT_CHARSET, DEFAULT_CONN_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    public static String doGet(final String url, final String params, final String charset) {
        return doGet(url, params, charset, DEFAULT_CONN_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    public static String doGet(final String url,
                               final String params,
                               final String charset,
                               final int connectTimeout,
                               final int soTimeout) {
        String result = null;
        try {
            String newUrl = addUrlParam(url, params, charset);
            byte[] b = doGet(newUrl, null, connectTimeout, soTimeout);
            result = new String(b, charset);
        }
        catch (UnsupportedEncodingException e) {
            log.error(String.format("[HttpUtil Get]doGet UnsupportedEncodingException error, url:%s",
                                    url),
                      e);
        }
        catch (ParseException e) {
            log.error(String.format("[HttpUtil Get]doGet ParseException error, url:%s", url), e);
        }
        return result;
    }

    public static String doGet(final String url,
                               final Map<String, String> params,
                               final String charset,
                               final int connectTimeout,
                               final int soTimeout) {
        String result = null;
        try {
            String newUrl = addUrlParam(url, params, charset);
            byte[] b = doGet(newUrl, null, connectTimeout, soTimeout);
            result = new String(b, charset);
        }
        catch (UnsupportedEncodingException e) {
            log.error(String.format("[HttpUtil Get]doGet UnsupportedEncodingException error, url:%s",
                                    url),
                      e);
        }
        return result;
    }

    /**
     * 发送Get请求
     * 
     * @param url
     *            访问的Http地址
     * @param headers
     *            Http头信息
     * @param connectTimeout
     *            连接超时
     * @param soTimeout
     *            连接后，读取时间超时
     * @return 返回请求的数据byte数组
     */
    public static byte[] doGet(final String url,
                               final Header[] headers,
                               final int connectTimeout,
                               final int soTimeout) {
        byte[] result = null;

        final HttpGet req = new HttpGet(url);
        try {
            if (log.isDebugEnabled()) {
                log.debug("[HttpUtil Get] begin invoke url: {}", url);
            }

            result = doRequest(req, headers, connectTimeout, soTimeout, null);

            if (log.isDebugEnabled()) {
                log.debug("[HttpUtil Get]Debug response, url : {}, response string : {}",
                          req.getURI(),
                          new String(result, ISO_8859_1));
            }
        }
        catch (UnsupportedEncodingException e) {
            log.error("[HttpUtil Get] UnsupportedEncodingException", e);
        }
        finally {
            req.releaseConnection();
        }
        return result;
    }

    public static String doPost(final String url, final String content) {
        return doPost(url, content, DEFAULT_CHARSET, DEFAULT_CONN_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    public static String doPost(final String url, final String content, final String charset) {
        return doPost(url, content, charset, DEFAULT_CONN_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    public static String doPost(final String url,
                                final Map<String, Object> map,
                                final String charset,
                                final int connectTimeout,
                                final int soTimeout) {
        String result = null;

        try {
            String str1 = JsonUtil.objectToJson(map);
            result = doPost(url, str1, charset, connectTimeout, soTimeout);
        }
        catch (JsonProcessingException e) {
            log.error("JsonProcessingException", e);
        }
        return result;
    }

    public static String doPost(final String url,
                                final String content,
                                final String charset,
                                final int connectTimeout,
                                final int soTimeout) {
        String result = null;

        Header[] headers = new Header[]{new BasicHeader("Content-type", "text/plain; charset=UTF-8")}; // "application/json")
                                                                                                       // };
        try {
            final byte[] b = doPost(url,
                                    content.getBytes(charset),
                                    headers,
                                    connectTimeout,
                                    soTimeout);
            result = new String(b, charset);
        }
        catch (UnsupportedEncodingException e) {
            log.error("doPost UnsupportedEncodingException", e);
        }
        return result;
    }

    public static byte[] doPost(final String url,
                                final byte[] content,
                                final Header[] headers,
                                final int connectTimeout,
                                final int soTimeout) {
        byte[] result = null;

        final HttpPost req = new HttpPost(url);
        try {
            req.setEntity(new ByteArrayEntity(content));

            if (log.isDebugEnabled()) {
                log.debug("[HttpUtil Post] begin invoke url: {}, content: {}",
                          url,
                          new String(content, ISO_8859_1));
            }

            result = doRequest(req, headers, connectTimeout, soTimeout, null);

            if (log.isDebugEnabled()) {
                log.debug("[HttpUtil Post]Debug response, url : {}, response string: {}",
                          req.getURI(),
                          new String(result, ISO_8859_1));
            }
        }
        catch (UnsupportedEncodingException e) {
            log.error("[HttpUtil Post] UnsupportedEncodingException", e);
        }
        finally {
            req.releaseConnection();
        }
        return result;
    }

    public static byte[] doRequest(final HttpRequestBase req,
                                   final Header[] headers,
                                   final int connectTimeout,
                                   final int soTimeout,
                                   List<Header> responseHeaders) {
        byte[] result = null;

        try {
            req.setHeaders(Default_Headers);
            if (headers != null && headers.length > 0) {
                for (Header h : headers) {
                    req.setHeader(h);
                }
            }

            final RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
                                                             .setSocketTimeout(soTimeout)
                                                             .setConnectTimeout(connectTimeout)
                                                             .setConnectionRequestTimeout(connectTimeout)
                                                             .build();

            req.setConfig(requestConfig);

            try (final CloseableHttpClient httpClient = getHttpClient()) {
                try (final CloseableHttpResponse response = httpClient.execute(req)) {
                    if (responseHeaders != null) {
                        for (Header h : response.getAllHeaders()) {
                            responseHeaders.add(h);
                        }
                    }
                    HttpEntity entity = response.getEntity();
                    try {
                        if (entity != null) {
                            // Header header = entity.getContentEncoding();
                            // if (header != null) {
                            // HeaderElement[] codecs = header.getElements();
                            // for (int i = 0; i < codecs.length; i++) {
                            // if (codecs[i].getName()
                            // .equalsIgnoreCase("gzip")) {
                            // entity = new GzipDecompressingEntity(entity);
                            // } else if (codecs[i].getName()
                            // .equalsIgnoreCase("deflate")) {
                            // entity = new DeflateDecompressingEntity(
                            // entity);
                            // }
                            // }
                            // }
                            result = EntityUtils.toByteArray(entity);
                        }
                    }
                    finally {
                        if (entity != null) {
                            entity.getContent().close();
                        }
                    }
                }
                // catch (Exception e) {
                // log.error(String.format("[HttpUtil %s]response error, url:%s",
                // req.getClass().getSimpleName(), req.getURI()), e);
                // return result;
                // } finally {
                // if (response != null) {
                // response.close();
                // }
                // if (httpClient != null) {
                // httpClient.close();
                // }
            }
            // } catch (ClientProtocolException e) {
            // log.error(String.format(
            // "[HttpUtil %s]ClientProtocolException url:%s,  error: ",
            // req.getClass().getSimpleName(), req.getURI()), e);
            // } catch (SocketTimeoutException e) {
            // log.error(String.format(
            // "[HttpUtil %s]socket timout url:%s,  error: ", req
            // .getClass().getSimpleName(), req.getURI()), e);
            // } catch (IOException e) {
            // log.error(String.format(
            // "[HttpUtil %s]IOException url:%s,  error: ", req.getClass()
            // .getSimpleName(), req.getURI()), e);
        }
        catch (Exception e) {
            log.error(String.format("[HttpUtil %s]Exception url:%s,  error: ",
                                    req.getClass().getSimpleName(),
                                    req.getURI()), e);
        }
        return result;
    }

    public static String addUrlParam(final String url,
                                     final Map<String, String> params,
                                     final String charset) {
        if (params == null || params.size() == 0) {
            return url;
        }
        String p = buildUrlParam(params, charset);
        return addUrlParam(url, p, charset);
    }

    public static String addUrlParam(final String url, final String param, final String charset) {
        if (param == null || param.length() == 0) {
            return url;
        }

        if (!url.contains("?")) {
            return url + "?" + param;
        } else {
            return url + "&" + param;
        }
    }

    public static String buildUrlParam(final Map<String, String> params, final String charset) {
        String p = null;
        if (params != null && !params.isEmpty()) {
            try {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                p = EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            catch (UnsupportedEncodingException e) {
                log.error("[HttpUtil buildUrlParam]UnsupportedEncodingException error: ", e);
            }
            catch (ParseException e) {
                log.error("[HttpUtil buildUrlParam]ParseException error:", e);
            }
            catch (IOException e) {
                log.error("[HttpUtil buildUrlParam]IOException error:", e);
            }
        }
        return p;
    }

    // /**
    // * HTTPS请求，默认超时为5S
    // *
    // * @param reqURL
    // * @param params
    // * @return
    // */
    // public static String doPostHttps(String reqURL, Map<String, String>
    // params) {
    //
    // String responseContent = null;
    //
    // HttpPost httpPost = new HttpPost(reqURL);
    // try {
    //
    // List<NameValuePair> formParams = new ArrayList<NameValuePair>();
    // httpPost.setEntity(new UrlEncodedFormEntity(formParams,
    // Consts.UTF_8));
    // httpPost.setConfig(requestConfig);
    // // 绑定到请求 Entry
    // for (Map.Entry<String, String> entry : params.entrySet()) {
    // formParams.add(new BasicNameValuePair(entry.getKey(), entry
    // .getValue()));
    // }
    // CloseableHttpResponse response = httpClient.execute(httpPost);
    // try {
    // // 执行POST请求
    // HttpEntity entity = response.getEntity(); // 获取响应实体
    // try {
    // if (null != entity) {
    // responseContent = EntityUtils.toString(entity,
    // Consts.UTF_8);
    // }
    // } finally {
    // if (entity != null) {
    // entity.getContent().close();
    // }
    // }
    // } finally {
    // if (response != null) {
    // response.close();
    // }
    // }
    // log.debug("requestURI : " + httpPost.getURI()
    // + ", responseContent: " + responseContent);
    // } catch (ClientProtocolException e) {
    // log.error("ClientProtocolException", e);
    // } catch (IOException e) {
    // log.error("IOException", e);
    // } finally {
    // httpPost.releaseConnection();
    // }
    // return responseContent;
    //
    // }

}