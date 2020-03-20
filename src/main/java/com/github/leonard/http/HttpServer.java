package com.github.leonard.http;

import com.github.leonard.http.handler.SocketChannelHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpServer {

	private static int port = 8080;

	public static void main(String[] args) throws InterruptedException {

		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.channel(NioServerSocketChannel.class).group(boss, worker).childOption(ChannelOption.SO_KEEPALIVE, true)
					.option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							// http解码器
							ch.pipeline().addLast("http-decoder", new HttpServerCodec());
							// 针对数据量大进行分片，可以不要此步骤
							 ch.pipeline().addLast("chunked-handler", new ChunkedWriteHandler());
							// 将HTTP消息的多个部分合成一条完整的HTTP消息
							 ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65535));
							// 设置WebSocket请求地址
							 ch.pipeline().addLast("websocket-protocol-handler", new WebSocketServerProtocolHandler("/ws"));
							// 自定义公共handler，用于分发HTTP和WebSocket
							ch.pipeline().addLast("socket-channel-handler", new SocketChannelHandler(ch));
						}
					});
			ChannelFuture f = b.bind(port).sync();
			System.out.println("Server start ...");
			// 等待服务器 socket 关闭 
			f.channel().closeFuture().sync();
		} finally {
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}

	}

}
