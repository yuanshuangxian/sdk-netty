package com.fqserver.server;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.IOException;
import java.util.Map;



import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fqserver.core.http.HttpUtil;
import com.fqserver.core.http.HttpUtil.ContentTypes;
import com.fqserver.core.http.HttpUtil.HeaderNames;
import com.fqserver.core.http.HttpUtil.HttpContentBean;
import com.fqserver.core.http.HttpUtil.ZipTypes;
import com.fqserver.core.json.CborUtil;
import com.fqserver.core.json.JsonUtil;
import com.fqserver.lang.encrypt.Rsa;

public class RequestHandlerV2 extends RequestHandler {

    protected String encryptType = null;
    protected int cCompressType = 0;
    protected int cContentSrcLen = 0;
    protected int cContentType = 0;
    protected String cContentCharset = HttpUtil.UTF_8;

    private static final CharSequence TYPE_PLAIN = HttpHeaders.newEntity(HttpUtil.TYPE_PLAIN);
    private static final CharSequence TYPE_BINARY = HttpHeaders.newEntity(HttpUtil.TYPE_BINARY);
    private static final CharSequence TYPE_JSON = HttpHeaders.newEntity(HttpUtil.TYPE_JSON);
    private static final CharSequence TYPE_CBOR = HttpHeaders.newEntity(HttpUtil.TYPE_CBOR);

    /**
     * 服务器默认的Get强制加密的类型, 默认get不加密
     * 
     * @return
     */
    protected String getServerGetEncrypt() {
        return null;
    }

    /**
     * 服务端默认的Post强制加密的类型, 默认AES加密
     * 
     * @return
     */
    protected String getServerPostEncrypt() {
        return "a";
    }

    /**
     * 是否跟随客户端的加密方式 1.服务器强制加密, true则客户端不加密的情况下才加密, false则服务器强制gzip加密 2.服务器不强制加密,
     * true则客户端加密的情况下才加密, false则不管客户端, 都不加密
     * 
     * @return
     */
    protected boolean isFollowClientEncrypt() {
        return true;
    }

    /**
     * 是否服务端强制加密
     * 
     * @return
     */
    protected boolean isServerForceEncrypt() {
        return false;
    }

    /**
     * 是否跟随客户端的加密方式 1.服务器强制加密, true则客户端不加密的情况下才加密, false则服务器强制gzip加密 2.服务器不强制加密,
     * true则客户端加密的情况下才加密, false则不管客户端, 都不加密
     * 
     * @return
     */
    protected boolean isFollowClientZip() {
        return true;
    }

    /**
     * 是否服务端强制压缩
     * 
     * @return
     */
    protected boolean isServerForceZip() {
        return false;
    }

    /**
     * 最小压缩大小, 大于该数值才压缩
     * 
     * @return
     */
    protected int getMinZipSize() {
        return 256;
    }

    /**
     * 服务端默认的强制压缩的类型, 默认gzip
     * 
     * @return
     */
    protected int getServerZipType() {
        return ZipTypes.GZIP;
    }

    @Override
    public void doPost() {

        // 解密
        encryptType = this.request.headers().get(HeaderNames.ORIGINAL_ENCRYPT);
        String b64AesKey = null;
        if (encryptType != null && encryptType.contains("c")) {
            b64AesKey = this.request.headers().get(HeaderNames.ENCRYPT_KEY);
        }

        // 解压
        String encode = this.request.headers().get(HeaderNames.ORIGINAL_ENCODING);
        if (encode != null && encode.length() > 0) {
            cCompressType = Integer.valueOf(encode);
        }
        if (cCompressType == ZipTypes.LZ4) {
            String srcLen = this.request.headers().get(HeaderNames.ORIGINAL_LENGTH);
            if (srcLen != null && srcLen.length() > 0) {
                cContentSrcLen = Integer.valueOf(srcLen);
            }
        }

        // 内容
        String contentType = this.request.headers().get(Names.CONTENT_TYPE);
        if (contentType != null && contentType.length() > 0) {
            if (contentType.contains(HttpUtil.STR_CBOR)) {
                cContentType = ContentTypes.CBOR;
            } else if (contentType.contains(HttpUtil.STR_JSON)) {
                cContentType = ContentTypes.JSON;
            } else if (contentType.contains(HttpUtil.STR_BINARY)) {
                cContentType = ContentTypes.BINARY;
            } else {
                cContentType = ContentTypes.PLAIN;
            }
            int s = contentType.indexOf("charset=");
            if (s > -1) {
                cContentCharset = contentType.substring(s + 8, contentType.length())
                                             .replaceAll("[=/\";\' ]*", "");
            }
        }

        // 初始化HttpContent
        // this.initHttpContent();

        // 处理解压解密
        if (cCompressType > 0 || (encryptType != null && encryptType.length() > 0)) {
            httpContent = HttpUtil.decryptContent(this.getHttpContent(),
                                                  encryptType,
                                                  b64AesKey,
                                                  Rsa.DefPriKey512,
                                                  cCompressType,
                                                  cContentSrcLen);
            // 传入byte[]进行post方法调用
            this.post(httpContent);
        } else {
            this.post(this.getHttpContent());
        }
    }

    /**
     * 获取post请求content(json格式解析为Map)
     * 
     * @return
     */
    @Override
    public Map<String, Object> getPostAttrs() {
        if (postAttributes == null) {
            try {
                if (cContentType == ContentTypes.CBOR) {
                    postAttributes = CborUtil.jsonToObject(getPostStr());
                } else if (cContentType == ContentTypes.JSON) {
                    postAttributes = JsonUtil.jsonToObject(getPostStr());
                } else if (cContentType == ContentTypes.BINARY) {
                    postAttributes = JsonUtil.jsonToObject(getHttpContent());
                } else if (cContentType == ContentTypes.PLAIN) {
                    postAttributes = JsonUtil.jsonToObject(getPostStr());
                }
            }
            catch (Exception e) {
                log.error("Get PostAtts error:", e);
                sendMap.put("errCode", 2);
                write(sendMap);
            }
        }

        return postAttributes;
    }

    /**
     * 直接传入解压之后的post的内容数组
     * 
     * @param content
     */
    public void post(byte[] content) {
        Map<String, Object> map = null;
        try {
            // 传入 byte[]数组作为参数进行post方法调用
            if (cContentType == ContentTypes.CBOR) {
                map = CborUtil.jsonToObject(content);
            } else if (cContentType == ContentTypes.JSON) {
                map = JsonUtil.jsonToObject(content);
            } else if (cContentType == ContentTypes.BINARY) {
                map = JsonUtil.jsonToObject(content);
            } else if (cContentType == ContentTypes.PLAIN) {
                this.post(new String(content, cContentCharset));
                return;
            }
        }
        catch (JsonParseException e) {
            log.error("post(byte[] content) JsonParseException error:", e);
        }
        catch (JsonMappingException e) {
            log.error("post(byte[] content) JsonMappingException error:", e);
        }
        catch (IOException e) {
            log.error("post(byte[] content) IOException error:", e);
        }
        this.post(map);
    }

    /**
     * 直接传入客户端传进来的Map数据
     * 
     * @param requestMap
     */
    public void post(Map<String, Object> requestMap) {
        this.post();
    }

    /**
     * 直接传入客户端传进来的String文本
     * 
     * @param content
     */
    public void post(String content) {
        Map<String, Object> map = null;
        try {
            map = JsonUtil.jsonToObject(content);
        }
        catch (JsonParseException e) {
            log.error("post(String content) JsonParseException error:", e);
        }
        catch (JsonMappingException e) {
            log.error("post(String content) JsonMappingException error:", e);
        }
        catch (IOException e) {
            log.error("post(String content) IOException error:", e);
        }
        this.post(map);
    }

    /**
     * 返回Map响应数据,解析为json字符串
     * 
     * @param respMap
     */
    @Override
    public void write(Map<String, Object> respMap) {

        try {
            if (cContentType == ContentTypes.CBOR) {
                writeResponse(CborUtil.objectToJsonByte(respMap));
            } else if (cContentType == ContentTypes.JSON) {
                writeResponse(JsonUtil.objectToJsonByte(respMap));
            } else if (cContentType == ContentTypes.BINARY) {
                writeResponse(JsonUtil.objectToJson(respMap).getBytes(cContentCharset));
            } else if (cContentType == ContentTypes.PLAIN) {
                writeResponse(JsonUtil.objectToJson(respMap).getBytes(cContentCharset));
            }
        }
        catch (Exception e) {
            log.error("post response Encode error", e);
            write("{\"status\":2}");
        }
    }

    @Override
    protected HttpContentBean beforeBuildResponse(final byte[] data) {

        // 压缩
        if (isServerForceZip()) {// 服务器前置压缩
            boolean isZip = true;
            if (isFollowClientZip()) {// 跟随客户端压缩方式, 跟随则使用客户端的方式,
                                      // 客户端无压缩使用服务端默认的压缩方式,
                                      // 不跟随则强制使用服务器压缩方式
                isZip = (cCompressType == 0);
            }
            if (isZip) {
                cCompressType = getServerZipType();
            }
        } else {
            if (!isFollowClientZip()) {// 跟随客户端压缩方式, 不跟随则不压缩, 跟随则随客户端的类型
                cCompressType = 0;
            }
        }

        // 加密
        if (isServerForceEncrypt()) { // 服务器强制加密
            boolean isZip = true;
            if (isFollowClientEncrypt()) { // 跟随客户端加密方式, 跟随则使用客户端的方式,
                                           // 客户端无压缩使用服务端默认的加密方式,
                                           // 不跟随则强制使用服务器加密方式
                isZip = (encryptType == null || encryptType.length() == 0);
            }
            if (isZip) {
                if (request.getMethod().equals(HttpMethod.GET)) {
                    encryptType = getServerGetEncrypt(); // 获取服务器默认的Get加密方式
                } else if (request.getMethod().equals(HttpMethod.POST)) {
                    encryptType = getServerPostEncrypt();// 获取服务器默认的Post加密方式
                }
            }
        } else {
            if (!isFollowClientEncrypt()) {// 跟随客户端加密方式, 不跟随则不加密, 跟随则随客户端的类型
                encryptType = null;
            }
        }

        // 处理返回数据的加密和压缩
        if (cCompressType > 0 || (encryptType != null && encryptType.length() > 0)) {
            return HttpUtil.encryptContent(data,
                                           encryptType,
                                           Rsa.DefPriKey512,
                                           cCompressType,
                                           getMinZipSize());
        }

        return null;
    }

    // @Override
    // protected void addServerHeader(final FullHttpResponse response) {
    // response.headers().set(HttpHeaders.Names.SERVER,
    // this.getServer().SERVER_NAME);
    // response.headers().set(HttpHeaders.Names.DATE,
    // this.getServer().curDateTime);
    // }

    @Override
    protected void afterBuildResponse(final HttpResponseStatus status,
                                      final FullHttpResponse response,
                                      final HttpContentBean bean) {
        super.afterBuildResponse(status, response, bean);

        if (null == bean) {
            return;
        }

        // 加密的头信息
        if (encryptType != null && encryptType.length() > 0) {
            response.headers().set(HeaderNames.ORIGINAL_ENCRYPT, encryptType);
        }
        // 加密的AES Key base64编码后的信息
        if (bean.encryptAesKey != null && bean.encryptAesKey.length() > 0) {
            response.headers().set(HeaderNames.ENCRYPT_KEY, bean.encryptAesKey);
        }

        // 压缩的头信息
        if (bean.compressType > 0) { // 压缩的头
            response.headers().set(HeaderNames.ORIGINAL_ENCODING, bean.compressType);
        }
        if (bean.compressType == ZipTypes.LZ4) { // LZ4
            response.headers()
                    .set(HeaderNames.ORIGINAL_LENGTH, Integer.toString(bean.contenSrcLen));
        }

        // 内容的格式
        if (cContentType == ContentTypes.BINARY) {
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, TYPE_BINARY);
        } else if (cContentType == ContentTypes.JSON) {
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, TYPE_JSON);
        } else if (cContentType == ContentTypes.CBOR) {
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, TYPE_CBOR);
        } else {
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, TYPE_PLAIN);
        }
    }

    @Override
    public void reset() {
        cCompressType = 0;
        cContentSrcLen = 0;
        cContentType = 0;
        encryptType = null;
        cContentCharset = UTF_8;

        super.reset();
    }

}
