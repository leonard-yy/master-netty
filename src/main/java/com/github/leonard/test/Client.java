package com.github.leonard.test;

import com.github.leonard.test.clientHandle.SimpleClientHandle;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

	private static int port = 9099;

	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup worker = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(worker);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// TODO Auto-generated method stub
					ch.pipeline().addLast(new SimpleClientHandle());
				}
			});

			ChannelFuture f = b.connect("127.0.0.1", port).sync();
			f.channel().closeFuture().sync();
		} finally {
			 worker.shutdownGracefully();
		}
	}

}
