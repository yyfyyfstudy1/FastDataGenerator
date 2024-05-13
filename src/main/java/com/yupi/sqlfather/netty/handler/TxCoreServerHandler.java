package com.rongda.system.tx.manager.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.rongda.system.tx.manager.netty.service.IActionService;
import com.rongda.system.tx.manager.netty.service.NettyService;
import com.rongda.system.tx.manager.framework.utils.SocketManager;
import com.rongda.system.tx.manager.framework.utils.SocketUtils;
import com.rongda.system.tx.manager.manager.ModelInfoManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.Executor;

/**
 * Handles a server-side channel.
 *
 * @author jhy
 */
@ChannelHandler.Sharable
@Slf4j
public class TxCoreServerHandler extends ChannelInboundHandlerAdapter { // (1)

	private NettyService nettyService;

	private Executor threadPool;


	public TxCoreServerHandler(Executor threadPool, NettyService nettyService) {
		this.threadPool = threadPool;
		this.nettyService = nettyService;
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg) {
		final String json = SocketUtils.getJson(msg);
		log.debug("request->" + json);
		threadPool.execute(() -> service(json, ctx));
	}

	private void service(String json, ChannelHandlerContext ctx) {
		if (StringUtils.isNotEmpty(json)) {
			JSONObject jsonObject = JSONObject.parseObject(json);
			String action = jsonObject.getString("a");
			String key = jsonObject.getString("k");
			JSONObject params = JSONObject.parseObject(jsonObject.getString("p"));
			String channelAddress = ctx.channel().remoteAddress().toString();

			IActionService actionService = nettyService.getActionService(action);

			String res = actionService.execute(channelAddress, key, params);

			JSONObject resObj = new JSONObject();
			resObj.put("k", key);
			resObj.put("d", res);

			SocketUtils.sendMsg(ctx, resObj.toString());

		}
	}

	@Override
	@SneakyThrows
	public void channelRegistered(ChannelHandlerContext ctx) {

		//是否到达最大上线连接数
		if (SocketManager.getInstance().isAllowConnection()) {
			SocketManager.getInstance().addClient(ctx.channel());
		} else {
			ctx.close();
		}
		super.channelRegistered(ctx);
	}

	@Override
	@SneakyThrows
	public void channelUnregistered(ChannelHandlerContext ctx) {

		SocketManager.getInstance().removeClient(ctx.channel());
		String modelName = ctx.channel().remoteAddress().toString();
		SocketManager.getInstance().outLine(modelName);

		ModelInfoManager.getInstance().removeModelInfo(modelName);
		super.channelUnregistered(ctx);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		log.info(cause.getMessage());
		//ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
		//心跳配置
		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				ctx.close();
			}
		}
	}

}
