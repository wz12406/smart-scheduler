package cn.wz.netty;

import cn.wz.netty.codec.RpcRequest;
import cn.wz.netty.codec.RpcResponse;
import cn.wz.netty.serialize.HessianSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author wangzhen
 * @version 1.0
 * @date 2017-03-15
 */
public class HttpRPCRequestHandler extends  SimpleChannelInboundHandler<RpcRequest> {
    public HttpRPCRequestHandler() {
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx,RpcRequest request) throws Exception {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setError("200");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("hello","world");
        map.put("zh_name","张三");
        rpcResponse.setResult(map);
        byte[] serialize = HessianSerializer.serialize(rpcResponse);
        ByteBuf buffer = Unpooled.copiedBuffer(serialize);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }


}
