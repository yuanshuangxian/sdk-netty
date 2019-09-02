package com.fqserver.server;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUploadServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    final static AtomicInteger counter = new AtomicInteger(0);

    private static Logger log = LoggerFactory.getLogger(FullHttpServerHandler.class);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (request.content().nioBuffer() != null) {
            handleUploadFile(ctx, request);
        }
    }

    @SuppressWarnings("resource")
    public void handleUploadFile(ChannelHandlerContext ctx, FullHttpRequest request)
            throws FileNotFoundException, IOException {
        final FileChannel fileChannel = new FileOutputStream("/tmp/" + counter.incrementAndGet()).getChannel();
        ByteBuffer buffer = request.content().nioBuffer();

        try {
            while (buffer.hasRemaining()) {
                // Write the ByteBuffer content to the file
                fileChannel.write(buffer);
            }

        }
        catch (Exception ex) {
            log.error("handleUploadFile error:", ex);
        }
        finally {
            fileChannel.close();
        }

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        response.headers().set(CONTENT_LENGTH, 0);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }
}