package cn.wz.netty;

import cn.wz.netty.codec.RpcRequest;
import cn.wz.netty.serialize.HessianSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * RPCRequest请求解码器，将发送的RPCRequest反序列化为对象，并透传到下一层
 * @author wangzhen
 * @version 1.0
 * @date 2017-03-15
 */
public class HttpRPCRequestDecodeHandler extends  SimpleChannelInboundHandler<FullHttpRequest> {
    public HttpRPCRequestDecodeHandler() {
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx,FullHttpRequest request) throws Exception {
        ByteBuf content = request.content();
        byte[] resive = new byte[content.readableBytes()];
        content.readBytes(resive);
        RpcRequest rpcRequest;
        rpcRequest = (RpcRequest) HessianSerializer.deserialize(resive,RpcRequest.class);
        ctx.fireChannelRead(rpcRequest);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                status, Unpooled.copiedBuffer("Failure: " + status.toString()
                + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE,
                mimeTypesMap.getContentType(file.getPath()));
    }
}
