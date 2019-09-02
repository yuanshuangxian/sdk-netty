package com.fqserver.core.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fqserver.core.http.HttpUtil.ContentTypes;
import com.fqserver.core.http.HttpUtil.HeaderNames;
import com.fqserver.core.http.HttpUtil.HttpContentBean;
import com.fqserver.core.http.HttpUtil.ZipTypes;
import com.fqserver.lang.encrypt.Rsa;
import com.fqserver.lang.util.Arrays;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.client.Response;
import com.ning.http.client.providers.netty.NettyAsyncHttpProvider;


public class AsyncHttpUtil {

    static final Logger log = LoggerFactory.getLogger(AsyncHttpUtil.class);

    public static List<byte[]> doAsyncPostByContentType(final String url,
                                                        final List<byte[]> contentList,
                                                        final int contentType,
                                                        final String encrypt,
                                                        final int compressType) {

        List<byte[]> resultList = new ArrayList<byte[]>();

        int count = contentList.size();

        CountDownLatch latch = new CountDownLatch(count);

        List<MyAsyncHandler> resps = new ArrayList<>();

        // 异步
        AsyncHttpClientConfig cg = new AsyncHttpClientConfig.Builder().setAllowPoolingConnections(true)
                                                                      .setConnectTimeout(HttpClientUtil.DEFAULT_CONN_TIMEOUT)
                                                                      .setReadTimeout(HttpClientUtil.DEFAULT_SO_TIMEOUT)
                                                                      .setMaxConnections(count)
                                                                      .build();

        try (AsyncHttpClient c = nettyProvider(cg)) {

            for (byte[] content : contentList) {

                HttpContentBean bean = HttpUtil.encryptContent(content,
                                                               encrypt,
                                                               Rsa.DefPubKey512,
                                                               compressType,
                                                               HttpUtil.MIN_COMPRESS_SIZE);

                Map<String, Collection<String>> headers = new HashMap<>();

                if (contentType == 0) {
                    headers.put(HeaderNames.CONTENT_TYPE,
                                Arrays.toList("text/plain; charset=UTF-8"));
                } else if (contentType == ContentTypes.JSON) {
                    headers.put(HeaderNames.CONTENT_TYPE,
                                Arrays.toList("application/json; charset=UTF-8"));
                } else if (contentType == ContentTypes.CBOR) {
                    headers.put(HeaderNames.CONTENT_TYPE,
                                Arrays.toList("application/cbor; charset=UTF-8"));
                }

                if (bean != null) {
                    // 加密的头信息
                    if (encrypt != null && encrypt.length() > 0) {
                        headers.put(HeaderNames.ORIGINAL_ENCRYPT, Arrays.toList(encrypt));
                    }
                    if (bean.encryptAesKey != null && bean.encryptAesKey.length() > 0) {
                        headers.put(HeaderNames.ENCRYPT_KEY, Arrays.toList(bean.encryptAesKey));
                    }

                    // 压缩的头信息
                    if (bean.compressType > 0) {
                        headers.put(HeaderNames.ORIGINAL_ENCODING,
                                    Arrays.toList(Integer.toString(bean.compressType)));
                    }
                    if (bean.compressType == ZipTypes.LZ4) {
                        headers.put(HeaderNames.ORIGINAL_LENGTH,
                                    Arrays.toList(Integer.toString(bean.contenSrcLen)));
                    }
                }

                // byte[] b = HttpToolkit.doRequest(req,
                // headers.toArray(new Header[] {}),
                // HttpClientUtil.DEFAULT_CONN_TIMEOUT,
                // HttpClientUtil.DEFAULT_SO_TIMEOUT, responseHeaders);

                // ListenableFuture<V>

                MyAsyncHandler ma = new MyAsyncHandler(latch);
                resps.add(ma);
                c.preparePost(url)
                 .setHeaders(headers)
                 .setBody(bean != null ? bean.content : content)
                 .execute(ma);

            }

            try {

                long time1 = System.currentTimeMillis();

                System.out.println(latch.await(1000, TimeUnit.MILLISECONDS));
                long time2 = System.currentTimeMillis();
                System.out.println("all success:" + (time2 - time1));

            }
            catch (InterruptedException e) {
                log.error("InterruptedException", e);

            }
            finally {

                // 返回结果
                for (int i = 0; i < resps.size(); i++) {

                    byte[] rs = resps.get(i).result;

                    System.out.println("i"
                                       + i
                                       + " resp code:"
                                       + resps.get(i).statusCode
                                       + ",resp bypeSize:"
                                       + (rs == null ? 0 : rs.length));

                    try {

                        if (resps.get(i).statusCode == 200) {

                            resultList.add(rs);

                        }

                        else {
                            resultList.add(null);
                        }

                    }
                    catch (Exception e) {
                        log.error("doAsyncPostByContentType Exception", e);
                        resultList.add(null);
                    }

                }

            }

        }

        return resultList;
    }

    public static AsyncHttpClient nettyProvider(AsyncHttpClientConfig config) {
        if (config == null) {
            return new AsyncHttpClient();
        } else {
            return new AsyncHttpClient(new NettyAsyncHttpProvider(config));
        }
    }

    private static class MyAsyncHandler extends AsyncCompletionHandler<Response> {

        private CountDownLatch latch;
        public byte[] result;
        public int statusCode;

        public MyAsyncHandler(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onThrowable(Throwable t) {

            latch.countDown();

            System.out.println("AsyncCompletionHandler error");
            log.error("AsyncCompletionHandler error:", t);
            super.onThrowable(t);
        }

        @Override
        public Response onCompleted(Response resp) throws Exception {

            byte[] b = resp.getResponseBodyAsBytes();

            if (b == null || b.length == 0) {
                return resp;
            }

            String sEncryptType = null;
            String sB64AesKey = null;
            int sCompressType = 0;
            int sContentSrcLen = 0;

            FluentCaseInsensitiveStringsMap headers = resp.getHeaders();
            if (headers.containsKey(HeaderNames.ORIGINAL_ENCRYPT)) {
                List<String> values = headers.get(HeaderNames.ORIGINAL_ENCRYPT);
                if (values.size() > 0) {
                    sEncryptType = values.get(0);
                }
            }
            if (headers.containsKey(HeaderNames.ENCRYPT_KEY)) {
                List<String> values = headers.get(HeaderNames.ENCRYPT_KEY);
                if (values.size() > 0) {
                    sB64AesKey = values.get(0);
                }
            }
            if (headers.containsKey(HeaderNames.ORIGINAL_LENGTH)) {
                List<String> values = headers.get(HeaderNames.ORIGINAL_LENGTH);
                if (values.size() > 0) {
                    sContentSrcLen = Integer.valueOf(values.get(0));
                }
            }
            if (headers.containsKey(HeaderNames.ORIGINAL_ENCODING)) {
                List<String> values = headers.get(HeaderNames.ORIGINAL_ENCODING);
                if (values.size() > 0) {
                    sCompressType = Integer.valueOf(values.get(0));
                }
            }

            result = HttpUtil.decryptContent(b,
                                             sEncryptType,
                                             sB64AesKey,
                                             Rsa.DefPubKey512,
                                             sCompressType,
                                             sContentSrcLen);

            statusCode = resp.getStatusCode();

            latch.countDown();

            return resp;
        }
    }

}
