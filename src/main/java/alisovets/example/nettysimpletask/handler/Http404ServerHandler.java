package alisovets.example.nettysimpletask.handler;

import static io.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

import java.net.InetSocketAddress;

import alisovets.example.nettysimpletask.status.StatusProcessor;


/**
* an http server handler that sends 404 request if there is wrong an uri in the request.  
*/
public class Http404ServerHandler extends ChannelInboundHandlerAdapter {
	
	private StatusProcessor statusProcessor;
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		if (!(msg instanceof HttpRequest)) {
			return;
		}
		HttpRequest req = (HttpRequest) msg;
		
		String uri = req.getUri();
		String ip = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
		
		statusProcessor = new StatusProcessor();
		statusProcessor.fixHttpOpenConnect(ip, uri);
		
		if (is100ContinueExpected(req)) {
			ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
		}
		
		final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND, Unpooled.wrappedBuffer(NOT_FOUND.toString().getBytes()));
		response.headers().set(CONTENT_TYPE, "text/plain");
		response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
		final int sentBytes = response.content().readableBytes();
		ctx.write(response).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				ctx.close();
				statusProcessor.fixCloseConnect(null, sentBytes);
			}
		});

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {

		cause.printStackTrace();
		ctx.close();
		if(statusProcessor != null){			
			statusProcessor.fixCloseConnect(null, 0);
		}
	}
}
