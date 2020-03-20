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
		 * ��������EventLoop���飬EventLoop ����൱��һ�������̣߳� ��Netty��������ʹ���IO������߳�
		 * ������ϣ�NioEventLoopGroup ��һ������I/O�����Ķ��߳��¼�ѭ����
		 * NettyΪ��ͬ���͵Ĵ����ṩ�˸���EventLoopGroupʵ�֡�
		 * �ڱ����У���������ʵ��һ����������Ӧ�ó�����˽�ʹ������NioEventLoopGroup�� ��һ����ͨ����Ϊ��boss�������ܴ�������ӡ�
		 * �ڶ�����ͨ����Ϊ��worker������boss�������Ӳ�ע�ᱻ���ܵ����ӵ�workerʱ�������������ӵ�������
		 * ʹ���˶����߳��Լ���ν�����ӳ�䵽������ͨ��ȡ����EventLoopGroupʵ�֣���������ͨ�����캯���������á�
		 */
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // ����̵߳Ľ��գ������ӷ��͸�worker
		EventLoopGroup workerGroup = new NioEventLoopGroup(); // ������ӵĴ���
		try {
			// 1������������
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			// 2����������������
			/**
			 * ����ѭ���߳��飬ǰ�����ڴ���ͻ��������¼����������ڴ�������IO(serverʹ����������,clientʹ��һ��) public
			 * ServerBootstrap group(EventLoopGroup group) public ServerBootstrap
			 * group(EventLoopGroup parentGroup, EventLoopGroup childGroup)
			 */
			serverBootstrap.group(bossGroup, workerGroup);
			/**
			 * ����ѡ�� ������Socket�ı�׼����(key��value): bootstrap.option(ChannelOption.SO_BACKLOG,
			 * 1024); bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			 */
			serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			// ���ڹ���socketchannel����
			serverBootstrap.channel(NioServerSocketChannel.class);

			/**
			 * �����Զ���ͻ���Handle������������ﴦ���Լ���ҵ��
			 */
			serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					// ע��handler
					ch.pipeline().addLast(new SimpleServerHandler());
				}
			});

			// �󶨶˿ڣ���ʼ���ս���������
			ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
			System.out.println("Server start ...");
			// �ȴ������� socket �ر� 
			channelFuture.channel().closeFuture().sync();
		} finally {
			// �����˳����ͷ��̳߳���Դ
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
