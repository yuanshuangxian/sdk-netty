package com.fqserver.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;

/**
 * @author Faye Li
 * 
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    private final HttpServer server;

    /**
	 *
	 */
    private final SslContext sslCtx;

    /**
     * HttpServer初始化函数
     * 
     * @param sslCtx
     *            传入的SslContext对象
     */
    public HttpServerInitializer(SslContext sslCtx, HttpServer svr) {
        this.sslCtx = sslCtx;
        this.server = svr;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        // pipeline.addLast(new HttpServerCodec());

        // 定义缓冲数据量
        // pipeline.addLast("aggegator", new HttpObjectAggregator(10));

        /**
         * http-request解码器 http服务器端对request解码
         */
        pipeline.addLast("decoder", new HttpRequestDecoder(4096, 8192, 8192, false));

        // 定义缓冲数据量 使用FullHttpServerHandler之前, 必须添加此HttpObjectAggregator
        pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));

        /**
         * http-response解码器 http服务器端对response编码
         */
        pipeline.addLast("encoder", new HttpResponseEncoder());

        // Remove the following line if you don't want automatic content
        // decompression.
        // pipeline.addLast("inflater", new HttpContentDecompressor());
        /**
         * 压缩 Compresses an HttpMessage and an HttpContent in gzip or deflate
         * encoding while respecting the "Accept-Encoding" header. If there is
         * no matching encoding, no compression is done.
         */
        // pipeline.addLast("deflater", new HttpContentCompressor(1));
        // pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

        pipeline.addLast("handler", new FullHttpServerHandler(this.server));

    }
}