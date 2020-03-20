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
							// http������
							ch.pipeline().addLast("http-decoder", new HttpServerCodec());
							// �������������з�Ƭ�����Բ�Ҫ�˲���
							 ch.pipeline().addLast("chunked-handler", new ChunkedWriteHandler());
							// ��HTTP��Ϣ�Ķ�����ֺϳ�һ��������HTTP��Ϣ
							 ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65535));
							// ����WebSocket�����ַ
							 ch.pipeline().addLast("websocket-protocol-handler", new WebSocketServerProtocolHandler("/ws"));
							// �Զ��幫��handler�����ڷַ�HTTP��WebSocket
							ch.pipeline().addLast("socket-channel-handler", new SocketChannelHandler(ch));
						}
					});
			ChannelFuture f = b.bind(port).sync();
			System.out.println("Server start ...");
			// �ȴ������� socket �ر� 
			f.channel().closeFuture().sync();
		} finally {
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}

	}

}
