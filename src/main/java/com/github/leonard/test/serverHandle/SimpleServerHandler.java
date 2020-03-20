package com.github.leonard.test.serverHandle;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleServerHandler extends ChannelInboundHandlerAdapter {
	/**
	 * ���������ڶ�ȡ�ͻ��˷��͵���Ϣ
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("SimpleServerHandler.channelRead");
		ByteBuf buf = (ByteBuf) msg;
		byte[] bytes = new byte[buf.readableBytes()];
		// msg�д洢����ByteBuf���͵����ݣ������ݶ�ȡ��byte[]��
		buf.readBytes(bytes);
		String message = new String(bytes);
		// ���ղ���ӡ�ͻ��˵���Ϣ
		System.out.println("Client said:" + message);
		// �ͷ���Դ�����кܹؼ�
		buf.release();

		// ��ͻ��˷�����Ϣ
		String response = "hello client!";
		// �ڵ�ǰ�����£����͵����ݱ���ת����ByteBuf����
		ByteBuf encoded = ctx.alloc().buffer(4 * response.length());
		encoded.writeBytes(response.getBytes());
		ctx.write(encoded);
		ctx.flush();
	}

	/**
	 * ���������������쳣
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// �������쳣�͹ر�����
		cause.printStackTrace();
		ctx.close();
	}

	/**
	 * ��Ϣ��ȡ��Ϻ����
	 * 
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
}