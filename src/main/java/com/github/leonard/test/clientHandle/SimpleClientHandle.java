package com.github.leonard.test.clientHandle;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleClientHandle extends ChannelInboundHandlerAdapter {

	/**
	 * ���������ڽ��շ���˷��͹�������Ϣ
	 * 
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("SimpleClientHandler.channelRead");
		ByteBuf result = (ByteBuf) msg;
		byte[] result1 = new byte[result.readableBytes()];
		result.readBytes(result1);
		System.out.println("Server said:" + new String(result1));
		result.release();
	}

	/**
	 * ���������������˷�����Ϣ
	 * 
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		String msg = "hello Server!";
		ByteBuf encoded = ctx.alloc().buffer(4 * msg.length());
		encoded.writeBytes(msg.getBytes());
		ctx.write(encoded);
		ctx.flush();
	}
	
	/**
	 * ���������ڴ����쳣
	 * 
	 * @param ctx
	 * @param cause
	 * @throws Exception
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// �������쳣�͹ر�����
		cause.printStackTrace();
		ctx.close();
	}

}
