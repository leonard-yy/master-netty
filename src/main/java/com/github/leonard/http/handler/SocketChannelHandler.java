package com.github.leonard.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class SocketChannelHandler extends SimpleChannelInboundHandler<Object> {

	private SocketChannel channel;
    private MsgHandler handler;

    public SocketChannelHandler(SocketChannel channel) {
          this.channel = channel;
    }
    
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof FullHttpRequest) {
            handler = new HttpMsgHandler();
            // 此处也可用WebSocketFrame自行读取数据
        }else if(msg instanceof TextWebSocketFrame) {
             handler = new WebSocketMsgHandler();
             handlerAdded(ctx);
        }else {
             ctx.close();
             channel.close();
             return;
        }
        transmit(ctx, msg);
	}
	public void transmit(ChannelHandlerContext ctx, Object msg) throws Exception {
        handler.channelRead(ctx, msg);
	}
	
	@Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
          if(handler != null) {
               handler.handlerRemoved(ctx);
          }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
          if(handler != null) {
               handler.exceptionCaught(ctx, cause);
          }
    }

}
