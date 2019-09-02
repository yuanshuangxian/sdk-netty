package com.fqserver.core.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fqserver.core.compress.LZ4Util;
import com.fqserver.core.compress.SnappyUtil;
import com.fqserver.core.http.HttpRequest.BaseHeader;
import com.fqserver.core.json.CborUtil;
import com.fqserver.core.json.JsonUtil;
import com.fqserver.lang.codec.Base64;
import com.fqserver.lang.codec.Hex;
import com.fqserver.lang.compress.GZip;
import com.fqserver.lang.compress.ZLib;
import com.fqserver.lang.encrypt.Aes;
import com.fqserver.lang.encrypt.Rsa;

public class HttpUtil {

    public static final String UTF_8 = "UTF-8";
    public static final String ISO_8859_1 = "ISO-8859-1";

    public static final String STR_PLAIN = "text/plain";
    public static final String STR_BINARY = "application/binary";
    public static final String STR_JSON = "application/json";
    public static final String STR_CBOR = "application/cbor";

    public static final String TMP_FMT = "%s; charset=%s";

    public static final String TYPE_PLAIN = String.format(TMP_FMT, STR_PLAIN, UTF_8);
    public static final String TYPE_BINARY = String.format(TMP_FMT, STR_BINARY, ISO_8859_1);
    public static final String TYPE_JSON = String.format(TMP_FMT, STR_JSON, UTF_8);
    public static final String TYPE_CBOR = String.format(TMP_FMT, STR_CBOR, UTF_8);

    public static class ContentTypes {
        /**
         * {@code "text"}
         */
        public static final int PLAIN = 0;

        /**
         * {@code "binary"}
         */
        public static final int BINARY = 1;

        /**
         * {@code "json"}
         */
        public static final int JSON = 2;

        /**
         * {@code "cbor"}
         */
        public static final int CBOR = 3;
    }

    public static class EncryptTypes {

        public static final String NONE = "";

        public static final String AES = "a";

        public static final String BASE64 = "b";

        public static final String AES_RSA = "c";

        public static final String RSA = "r";

        public static final String HEX = "h";

    }

    public static class ZipTypes {

        /**
         * {@code "none"}
         */
        public static final int NONE = 0;
        /**
         * {@code "gzip"}
         */
        public static final int GZIP = 1;

        /**
         * {@code "deflate"}
         */
        public static final int DEFLATE = 2;

        /**
         * {@code "snappy"}
         */
        public static final int SNAPPY = 3;

        /**
         * {@code "lz4"}
         */
        public static final int LZ4 = 4;
    }

    public static class HeaderValues {

        /**
         * {@code "none"}
         */
        public static final String NONE = "";

        /**
         * {@code "gzip"}
         */
        public static final String GZIP = "gzip";

        /**
         * {@code "deflate"}
         */
        public static final String DEFLATE = "deflate";
        /**
         * {@code "snappy"}
         */
        public static final String SNAPPY = "snappy";

        /**
         * {@code "lz4"}
         */
        public static final String LZ4 = "lz4";
    }

    public static class HeaderNames {
        /** RFC 1945 (HTTP/1.0) Section 10.5, RFC 2616 (HTTP/1.1) Section 14.17 */
        public static final String CONTENT_TYPE = "Content-Type";

        /**
         * {@code "Original-Encoding"}
         */
        public static final String ORIGINAL_ENCODING = "Original-Encoding";
        /**
         * {@code "Content-Encrypt"}
         */
        public static final String ORIGINAL_ENCRYPT = "Original-Encrypt";
        /**
         * {@code "Encrypt-Key"}
         */
        public static final String ENCRYPT_KEY = "Encrypt-Key";
        /**
         * {@code "Encrypt-Data"}
         */
        public static final String ENCRYPT_DATA = "Encrypt-Data";
        /**
         * {@code "Original-Length"}
         */
        public static final String ORIGINAL_LENGTH = "Original-Length";
    }

    static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    public static final int MIN_COMPRESS_SIZE = 256;

    public static Map<String, Object> doPostByJson(final String url,
                                                   final Map<String, Object> input,
                                                   final String encrypt,
                                                   final int compressType) {
        return doPostByJson(url,
                            input,
                            encrypt,
                            compressType,
                            HttpClientUtil.DEFAULT_CONN_TIMEOUT,
                            HttpClientUtil.DEFAULT_SO_TIMEOUT);
    }

    public static Map<String, Object> doPostByJson(final String url,
                                                   final Map<String, Object> input,
                                                   final String encrypt,
                                                   final int compressType,
                                                   final int connTimeout,
                                                   final int soTimeout) {
        try {
            byte[] content = JsonUtil.objectToJsonByte(input);
            byte[] b = doPostByContentType(url,
                                           content,
                                           ContentTypes.JSON,
                                           encrypt,
                                           compressType,
                                           connTimeout,
                                           soTimeout);

            if (b != null) {
                return JsonUtil.jsonToObject(b);
            }
        }
        catch (JsonProcessingException e) {
            log.error("doPostByJson JsonProcessingException", e);
        }
        catch (IOException e) {
            log.error("doPostByJson IOException", e);
        }
        return null;
    }

    public static Map<String, Object> doPostByCbor(final String url,
                                                   final Map<String, Object> input,
                                                   final String encrypt,
                                                   final int compressType) {
        return doPostByCbor(url,
                            input,
                            encrypt,
                            compressType,
                            HttpClientUtil.DEFAULT_CONN_TIMEOUT,
                            HttpClientUtil.DEFAULT_SO_TIMEOUT);
    }

    public static Map<String, Object> doPostByCbor(final String url,
                                                   final Map<String, Object> input,
                                                   final String encrypt,
                                                   final int compressType,
                                                   final int connTimeout,
                                                   final int soTimeout) {
        try {
            byte[] content = CborUtil.objectToJsonByte(input);
            byte[] b = doPostByContentType(url,
                                           content,
                                           ContentTypes.CBOR,
                                           encrypt,
                                           compressType,
                                           connTimeout,
                                           soTimeout);

            if (b != null) {
                return CborUtil.jsonToObject(b);
            }

        }
        catch (JsonProcessingException e) {
            log.error("doPostByCbor CborProcessingException", e);
        }
        catch (IOException e) {
            log.error("doPostByCbor IOException", e);
        }
        return null;
    }

    public static String doPost(final String url,
                                final String input,
                                final String encrypt,
                                final int compressType) {
        try {
            byte[] content = input.getBytes(UTF_8);
            byte[] b = doPostByContentType(url, content, ContentTypes.JSON, encrypt, compressType);

            return new String(b, UTF_8);
        }
        catch (UnsupportedEncodingException e) {
            log.error("doPost UnsupportedEncodingException", e);
        }
        return null;
    }

    public static byte[] doPostByContentType(final String url,
                                             final byte[] content,
                                             final int contentType,
                                             final String encrypt,
                                             final int compressType,
                                             final int connTimeout,
                                             final int soTimeout) {

        byte[] result = null;

        HttpContentBean bean = HttpUtil.encryptContent(content,
                                                       encrypt,
                                                       Rsa.DefPubKey512,
                                                       compressType,
                                                       HttpUtil.MIN_COMPRESS_SIZE);

        List<Header> headers = new ArrayList<Header>();

        if (contentType == 0) {
            headers.add(new BasicHeader(HeaderNames.CONTENT_TYPE, TYPE_PLAIN));
        } else if (contentType == ContentTypes.BINARY) {
            headers.add(new BasicHeader(HeaderNames.CONTENT_TYPE, TYPE_BINARY));
        } else if (contentType == ContentTypes.JSON) {
            headers.add(new BasicHeader(HeaderNames.CONTENT_TYPE, TYPE_JSON));
        } else if (contentType == ContentTypes.CBOR) {
            headers.add(new BasicHeader(HeaderNames.CONTENT_TYPE, TYPE_CBOR));
        }

        if (bean != null) {
            // 加密的头信息
            if (encrypt != null && encrypt.length() > 0) {
                headers.add(new BasicHeader(HeaderNames.ORIGINAL_ENCRYPT, encrypt));
            }
            if (bean.encryptAesKey != null && bean.encryptAesKey.length() > 0) {
                headers.add(new BasicHeader(HeaderNames.ENCRYPT_KEY, bean.encryptAesKey));
            }

            // 压缩的头信息
            if (bean.compressType > 0) {
                headers.add(new BasicHeader(HeaderNames.ORIGINAL_ENCODING,
                                            Integer.toString(bean.compressType)));
            }
            if (bean.compressType == ZipTypes.LZ4) {
                headers.add(new BasicHeader(HeaderNames.ORIGINAL_LENGTH,
                                            Integer.toString(bean.contenSrcLen)));
            }
        }

        final HttpPost req = new HttpPost(url);
        try {
            req.setEntity(new ByteArrayEntity(bean != null ? bean.content : content));

            List<Header> responseHeaders = new ArrayList<Header>();
            byte[] b = HttpToolkit.doRequest(req,
                                             headers.toArray(new Header[]{}),
                                             connTimeout,
                                             soTimeout,
                                             responseHeaders);

            if (b == null || b.length == 0)
                return result;

            String sEncryptType = null;
            String sB64AesKey = null;
            int sCompressType = 0;
            int sContentSrcLen = 0;

            for (Header h : responseHeaders) {
                if (h.getName() != null) {
                    if (h.getName().equalsIgnoreCase(HeaderNames.ORIGINAL_ENCRYPT)) {
                        sEncryptType = h.getValue();
                    } else if (h.getName().equalsIgnoreCase(HeaderNames.ENCRYPT_KEY)) {
                        sB64AesKey = h.getValue();
                    } else if (h.getName().equalsIgnoreCase(HeaderNames.ORIGINAL_LENGTH)) {
                        if (h.getValue() != null && h.getValue().length() > 0) {
                            sContentSrcLen = Integer.valueOf(h.getValue());
                        }
                    } else if (h.getName().equalsIgnoreCase(HeaderNames.ORIGINAL_ENCODING)) {
                        if (h.getValue() != null && h.getValue().length() > 0) {
                            sCompressType = Integer.valueOf(h.getValue());
                        }
                    }
                }
            }

            result = HttpUtil.decryptContent(b,
                                             sEncryptType,
                                             sB64AesKey,
                                             Rsa.DefPubKey512,
                                             sCompressType,
                                             sContentSrcLen);
        }
        finally {
            req.releaseConnection();
        }
        return result;

    }

    public static byte[] doPostByContentType(final String url,
                                             final byte[] content,
                                             final int contentType,
                                             final String encrypt,
                                             final int compressType) {

        return doPostByContentType(url,
                                   content,
                                   contentType,
                                   encrypt,
                                   compressType,
                                   HttpClientUtil.DEFAULT_CONN_TIMEOUT,
                                   HttpClientUtil.DEFAULT_SO_TIMEOUT);

    }

    public static String sendPost(final String url,
                                  final String input,
                                  final String encrypt,
                                  final int compressType) {
        String result = null;

        try {
            byte[] content = input.getBytes(UTF_8);

            HttpContentBean bean = HttpUtil.encryptContent(content,
                                                           encrypt,
                                                           Rsa.DefPubKey512,
                                                           compressType,
                                                           HttpUtil.MIN_COMPRESS_SIZE);

            List<BaseHeader> headers = new ArrayList<BaseHeader>();
            headers.add(new BaseHeader(HeaderNames.CONTENT_TYPE, TYPE_JSON));

            if (bean != null) {
                // 加密的头信息
                if (encrypt != null && encrypt.length() > 0) {
                    headers.add(new BaseHeader(HeaderNames.ORIGINAL_ENCRYPT, encrypt));
                }
                if (bean.encryptAesKey != null && bean.encryptAesKey.length() > 0) {
                    headers.add(new BaseHeader(HeaderNames.ENCRYPT_KEY, bean.encryptAesKey));
                }

                // 压缩的头信息
                if (bean.compressType > 0) {
                    headers.add(new BaseHeader(HeaderNames.ORIGINAL_ENCODING, bean.compressType));
                }
                if (bean.compressType == ZipTypes.LZ4) {
                    headers.add(new BaseHeader(HeaderNames.ORIGINAL_LENGTH,
                                               Integer.toString(bean.contenSrcLen)));
                }
            }

            try {
                List<BaseHeader> responseHeaders = new ArrayList<BaseHeader>();
                byte[] b = HttpRequest.doRequest(url,
                                                 bean != null ? bean.content : content,
                                                 headers.toArray(new BaseHeader[]{}),
                                                 HttpClientUtil.DEFAULT_CONN_TIMEOUT,
                                                 HttpClientUtil.DEFAULT_SO_TIMEOUT,
                                                 responseHeaders);

                if (b == null || b.length == 0)
                    return result;

                String sEncryptType = null;
                String sB64AesKey = null;
                int sCompressType = 0;
                int cContentSrcLen = 0;

                for (BaseHeader h : responseHeaders) {
                    if (h.getName() != null) {
                        if (h.getName().equalsIgnoreCase(HeaderNames.ORIGINAL_ENCRYPT)) {
                            sEncryptType = h.getValue();
                        } else if (h.getName().equalsIgnoreCase(HeaderNames.ENCRYPT_KEY)) {
                            sB64AesKey = h.getValue();
                        } else if (h.getName().equalsIgnoreCase(HeaderNames.ORIGINAL_LENGTH)) {
                            if (h.getValue() != null && h.getValue().length() > 0) {
                                cContentSrcLen = Integer.valueOf(h.getValue());
                            }
                        } else if (h.getName().equalsIgnoreCase(HeaderNames.ORIGINAL_ENCODING)) {
                            if (h.getValue() != null && h.getValue().length() > 0) {
                                sCompressType = Integer.valueOf(h.getValue());
                            }
                        }
                    }
                }

                b = HttpUtil.decryptContent(b,
                                            sEncryptType,
                                            sB64AesKey,
                                            Rsa.DefPubKey512,
                                            sCompressType,
                                            cContentSrcLen);

                result = new String(b, UTF_8);
            }
            finally {
                // req.releaseConnection();
            }
        }
        catch (UnsupportedEncodingException e) {
            log.error("doPost UnsupportedEncodingException", e);
        }
        return result;
    }

    public static byte[] decryptContent(final byte[] data,
                                        final String encrypt,
                                        final String b64AesKey,
                                        final Key rsaKey,
                                        final int compressType,
                                        final int contenSrcLen) {
        if (data == null) {
            return new byte[0];
        }
        byte[] recData = data;
        try {

            // 解密
            if (encrypt != null && encrypt.length() > 0) {
                for (int i = encrypt.length() - 1; i >= 0; i--) {
                    char c = encrypt.charAt(i);
                    if (c == EncryptTypes.AES.charAt(0)) {
                        recData = Aes.decrypt(Aes.MSG_AES_PASSWORD, recData);
                    } else if (c == EncryptTypes.BASE64.charAt(0)) {
                        recData = Base64.decode(recData);
                    } else if (c == EncryptTypes.AES_RSA.charAt(0)) {
                        String encAesKey = Base64.decodeRaw(b64AesKey);

                        // 解密后的AES秘钥
                        String aesKey = Rsa.unwrapKey(encAesKey, rsaKey);

                        // 解密后的明文
                        recData = Aes.decrypt(aesKey, recData);

                    } else if (c == EncryptTypes.RSA.charAt(0)) {
                        recData = Rsa.decrypt(recData, rsaKey);
                    } else if (c == EncryptTypes.HEX.charAt(0)) {
                        recData = Hex.decodeHex(recData);
                    }
                }
            }

            // 解压
            if (compressType > 0) {
                if (compressType == 1) {
                    recData = GZip.decompress(recData);
                } else if (compressType == 2) {
                    recData = ZLib.decompress(recData);
                } else if (compressType == 3) {
                    recData = SnappyUtil.decompress(recData);
                } else if (compressType == 4) {
                    recData = LZ4Util.decompress(recData, contenSrcLen);
                }
            }
        }
        catch (Exception e) {
            log.error("Decrypt Http Content error:", e);
        }
        return recData;
    }

    public static HttpContentBean encryptContent(final byte[] data,
                                                 final String encrypt,
                                                 final Key rsaKey,
                                                 final int compressType,
                                                 final int minCompressSize) {
        if (null == data) {
            return null;
        }

        // 是否加密压缩过
        boolean isDealed = false;

        byte[] resData = data;
        String encryptAesKey = null;
        int cCompressType = 0;
        int cContentSrcLen = 0;
        try {

            // 压缩
            // 大于100个字节才压缩
            if (resData.length > minCompressSize) {
                if (compressType == 1) {
                    resData = GZip.compress(resData);
                    cCompressType = ZipTypes.GZIP;

                    isDealed = true;
                } else if (compressType == 2) {
                    resData = ZLib.compress(resData);
                    cCompressType = ZipTypes.DEFLATE;

                    isDealed = true;
                } else if (compressType == 3) {
                    resData = SnappyUtil.compress(resData);
                    cCompressType = ZipTypes.SNAPPY;

                    isDealed = true;
                } else if (compressType == 4) {
                    cContentSrcLen = resData.length;
                    resData = LZ4Util.compress(resData);
                    cCompressType = ZipTypes.LZ4;

                    isDealed = true;
                }
            }

            // 加密
            if (encrypt != null && encrypt.length() > 0) {
                for (int i = 0; i < encrypt.length(); i++) {
                    char c = encrypt.charAt(i);
                    if (c == EncryptTypes.AES.charAt(0)) {
                        resData = Aes.encrypt(Aes.MSG_AES_PASSWORD, resData);

                        isDealed = true;
                    } else if (c == EncryptTypes.BASE64.charAt(0)) {
                        resData = Base64.encode(resData);

                        isDealed = true;
                    } else if (c == EncryptTypes.AES_RSA.charAt(0)) {
                        String aesKey = Aes.genAlphaNumKey();

                        String encAesKey = Rsa.wrapKey(aesKey, rsaKey);

                        encryptAesKey = Base64.encodeRaw(encAesKey);

                        // 解密后的明文
                        resData = Aes.encrypt(aesKey, resData);

                        isDealed = true;
                    } else if (c == EncryptTypes.RSA.charAt(0)) {
                        resData = Rsa.encrypt(resData, rsaKey);

                        isDealed = true;
                    } else if (c == EncryptTypes.HEX.charAt(0)) {
                        resData = Hex.encodeHex(resData);

                        isDealed = true;
                    }
                }
            }
        }
        catch (Exception e) {
            log.error("Encrypt Http Content error:", e);
        }

        if (isDealed) {
            return new HttpContentBean(resData, encryptAesKey, cCompressType, cContentSrcLen);
        }
        return null;
    }

    public static class HttpContentBean {
        public byte[] content;
        public String encryptAesKey;
        public int compressType;
        public int contenSrcLen;

        public HttpContentBean(byte[] c, String aesKey, int compress, int srcLen) {
            content = c;
            encryptAesKey = aesKey;
            compressType = compress;
            contenSrcLen = srcLen;
        }

        public void init(byte[] c, String aesKey, int compress, int srcLen) {
            content = c;
            encryptAesKey = aesKey;
            compressType = compress;
            contenSrcLen = srcLen;
        }
    }
}
