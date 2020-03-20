package com.github.leonard.http.handler;

import io.netty.channel.ChannelHandlerContext;

public interface MsgHandler {
	void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;
    // ���¿ͻ�������
    void handlerAdded(ChannelHandlerContext ctx) throws Exception; 
    // �пͻ����˳�
    void handlerRemoved(ChannelHandlerContext ctx) throws Exception; 
    // �пͻ��˳����쳣
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);
}
