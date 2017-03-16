package cn.wz.netty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author wangzhen
 * @date 2017-03-15
 * @version 1.0
 */
public class HttpServer {


    public void run(final int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch)
						throws Exception {
						ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());
						ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
						ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
						ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
						ch.pipeline().addLast("httpRPCServerHandler", new HttpRPCRequestDecodeHandler());
						ch.pipeline().addLast("httpRPCRequestHandler", new HttpRPCRequestHandler());
					}
				});
			ChannelFuture future = b.bind("127.0.0.1", port).sync();
			System.out.println("netty service已启动，访问地址: " + "http://127.0.0.1:"
				+ port);
			future.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
    }

    public static void main(String[] args) throws Exception {
		int port = 8080;
		new HttpServer().run(port);
    }
}
