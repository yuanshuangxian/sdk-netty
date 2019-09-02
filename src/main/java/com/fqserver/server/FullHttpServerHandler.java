package com.fqserver.server;

import com.fqserver.core.utils.Pools;
import com.fqserver.utils.time.DateUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

import java.net.InetSocketAddress;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FullHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final HttpServer server;

    private FullHttpRequest httpRequest;

    private RequestHandler reqHandler;

    private String requestUri;

    private static Logger log = LoggerFactory.getLogger(FullHttpServerHandler.class);

    public FullHttpServerHandler(HttpServer svr) {
        this.server = svr;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        messageReceived(ctx, msg);
    }

    public void messageReceived(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        httpRequest = msg;

        URI uri = new URI(httpRequest.getUri());
        String path = requestUri = uri.getPath();

        if (log.isInfoEnabled()) {
            String clientIP = httpRequest.headers().get("X-Forwarded-For");
            if (clientIP == null) {
                InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
                clientIP = insocket.getAddress().getHostAddress();
            }
            String reqStr = "clientIP:"
                            + clientIP
                            + "\t request uri=="
                            + requestUri
                            + "\t"
                            + "Requser Method:"
                            + httpRequest.getMethod()
                            + "\t Access time:"
                            + DateUtil.now();
            log.info(reqStr);
        }

        Class<? extends RequestHandler> cls = null;
        if (UrlMap.urlMap.containsKey(path)) {
            cls = UrlMap.urlMap.get(path);
        } else if (path.contains("/referrer")) {
            cls = UrlMap.urlMap.get("/referrer");
        } else {
            cls = RequestHandler.class;
        }

        try {

            if (this.server.getConfig().useReqObjectPool) {
                // reqHandler =
                // RequestHandlerFactory.getPool().borrowObject(cls);
                reqHandler = Pools.obtain(cls);
                if (log.isDebugEnabled()) {
                    log.debug("obtainObject ed:" + reqHandler);
                }
            } else {
                reqHandler = (RequestHandler) cls.newInstance();
                if (log.isDebugEnabled()) {
                    log.debug("newInstance ed:" + reqHandler);
                }
            }
        

            reqHandler.setServer(this.server);
            reqHandler.setRequest(httpRequest);
            reqHandler.setCtx(ctx);

            reqHandler.setRequestPrefix(ServerContext.getLocalRequestPrefix());

            // Url Request 计数
            ServerContext.addUrlRequest(path);

            // Get请求
            if (httpRequest.getMethod().equals(HttpMethod.GET)) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Get Req:" + uri.getPath());
                    }
                    long time1 = System.currentTimeMillis();
                    reqHandler.doGet();
                    long time2 = System.currentTimeMillis();

                    if (log.isInfoEnabled()) {
                        log.info(requestUri + "Get Time consuming " + (time2 - time1) + "ms");
                    }
                }
                catch (Exception e1) {
                    log.error("Request Handler get error:", e1);
                }
                reset();
                return;
            } else if (httpRequest.getMethod().equals(HttpMethod.POST)) { // Post请求
                try {
                    long time1 = System.currentTimeMillis();
                    reqHandler.doPost();
                    long time2 = System.currentTimeMillis();

                    if (log.isInfoEnabled()) {
                        log.info(requestUri + " Post Time consuming " + (time2 - time1) + "ms");
                    }
                }
                catch (Exception e1) {
                    log.error("Request Handler post error:", e1);
                }
                reset();
                return;
            } else {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Get Req:" + uri.getPath());
                    }
                    long time1 = System.currentTimeMillis();
                    reqHandler.doGet();
                    long time2 = System.currentTimeMillis();

                    if (log.isInfoEnabled()) {
                        log.info(requestUri
                                 + "Default Get Time consuming "
                                 + (time2 - time1)
                                 + "ms");
                    }
                }
                catch (Exception e1) {
                    log.error("Request Handler get error:", e1);
                }
                reset();
                return;
            }
        }
        catch (Exception e1) {
            log.error("Request Handler error:", e1);
            reset();
            ctx.channel().close();
            return;
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // ctx.flush();
        super.channelReadComplete(ctx);
    }

    private void reset() {

        httpRequest = null;

        requestUri = null;

        if (reqHandler != null) {
            if (this.server.getConfig().useReqObjectPool) {
                try {
                    // RequestHandlerFactory.getPool().returnObject(
                    // reqHandler.getClass(), reqHandler);
                    Pools.free(reqHandler);

                }
                catch (Exception e) {
                    log.error("freeObject error", e);
                }
            }
            reqHandler = null;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (log.isWarnEnabled()) {
            log.warn("netty handler warn", cause);
        }
        ctx.channel().close();
    }

}