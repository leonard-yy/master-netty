package com.github.leonard.test;

import com.github.leonard.test.serverHandle.SimpleServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
	private static final int port = 9099;

	public static void main(String[] args) throws InterruptedException {

		/**
		 * 创建两个EventLoop的组，EventLoop 这个相当于一个处理线程， 是Netty接收请求和处理IO请求的线程
		 * 相关资料：NioEventLoopGroup 是一个处理I/O操作的多线程事件循环。
		 * Netty为不同类型的传输提供了各种EventLoopGroup实现。
		 * 在本例中，我们正在实现一个服务器端应用程序，因此将使用两个NioEventLoopGroup。 第一个，通常称为“boss”，接受传入的连接。
		 * 第二个，通常称为“worker”，当boss接受连接并注册被接受的连接到worker时，处理被接受连接的流量。
		 * 使用了多少线程以及如何将它们映射到创建的通道取决于EventLoopGroup实现，甚至可以通过构造函数进行配置。
		 */
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // 完成线程的接收，将连接发送给worker
		EventLoopGroup workerGroup = new NioEventLoopGroup(); // 完成连接的处理
		try {
			// 1、创建启动类
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			// 2、配置启动参数等
			/**
			 * 设置循环线程组，前者用于处理客户端连接事件，后者用于处理网络IO(server使用两个参数,client使用一个) public
			 * ServerBootstrap group(EventLoopGroup group) public ServerBootstrap
			 * group(EventLoopGroup parentGroup, EventLoopGroup childGroup)
			 */
			serverBootstrap.group(bossGroup, workerGroup);
			/**
			 * 设置选项 参数：Socket的标准参数(key，value): bootstrap.option(ChannelOption.SO_BACKLOG,
			 * 1024); bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			 */
			serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			// 用于构造socketchannel工厂
			serverBootstrap.channel(NioServerSocketChannel.class);

			/**
			 * 传入自定义客户端Handle（服务端在这里处理自己的业务）
			 */
			serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					// 注册handler
					ch.pipeline().addLast(new SimpleServerHandler());
				}
			});

			// 绑定端口，开始接收进来的连接
			ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
			System.out.println("Server start ...");
			// 等待服务器 socket 关闭 
			channelFuture.channel().closeFuture().sync();
		} finally {
			// 优雅退出，释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
