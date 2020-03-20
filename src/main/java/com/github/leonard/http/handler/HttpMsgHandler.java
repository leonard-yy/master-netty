package com.github.leonard.http.handler;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.CharsetUtil;

public class HttpMsgHandler implements MsgHandler {

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		FullHttpRequest request = (FullHttpRequest) msg;
		// 处理请求
		try {
			// 1.获取URI
			String uri = request.uri();
			// 2.获取请求体
			ByteBuf buf = request.content();
			String content = buf.toString(CharsetUtil.UTF_8);
			// 3.获取请求方法
			HttpMethod method = request.method();
			// 4.获取请求头
			HttpHeaders headers = request.headers();
			// 5.根据method，确定不同的逻辑
			if (method.equals(HttpMethod.GET)) {
				Map<String, Object> ret = new HashMap<String, Object>();
				ret.put("SUCCESS", true);
				FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
						HttpResponseStatus
						.OK, 
						Unpooled.copiedBuffer(JSONObject.toJSONString(ret), CharsetUtil.UTF_8));
				
				resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
				// 2.发送
				// 注意必须在使用完之后，close channel
				ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
			}
			if (method.equals(HttpMethod.POST)) {
			}
			if (method.equals(HttpMethod.PUT)) {
			}
			if (method.equals(HttpMethod.DELETE)) {
			}
		} finally {
			request.release();
		}
	}

	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("新增一条请求...");
	}

	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		System.out.println("关闭一条请求...");
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.out.println("异常...");
	}

}
