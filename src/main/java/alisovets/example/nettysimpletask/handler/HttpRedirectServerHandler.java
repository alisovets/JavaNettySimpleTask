package alisovets.example.nettysimpletask.handler;

import static io.netty.handler.codec.http.HttpHeaders.Names.CACHE_CONTROL;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import static io.netty.handler.codec.http.HttpHeaders.Values.NO_CACHE;
import static io.netty.handler.codec.http.HttpResponseStatus.MOVED_PERMANENTLY;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.net.InetSocketAddress;
import java.util.List;

import alisovets.example.nettysimpletask.status.StatusProcessor;

/**
* an http server handler that redirects on the uri which is specified in the uri request parameter
*/
public class HttpRedirectServerHandler extends ChannelInboundHandlerAdapter {
	private final static String REDIRECT_URI_PATH = "/redirect";
	private final static String FORWARD_URL_KEY = "url";

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
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(	uri);
		if(!queryStringDecoder.path().equals(REDIRECT_URI_PATH)){
			ctx.fireChannelRead(msg);
			return;
		}
		
		String ip = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
		
		//TODO it must not be this
		statusProcessor = new StatusProcessor();
		statusProcessor.fixHttpOpenConnect(ip, uri);
		
		List<String> urls = queryStringDecoder.parameters()
				.get(FORWARD_URL_KEY);

		if (urls != null) {
			final String forwardUrl = urls.get(0);
			final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, MOVED_PERMANENTLY);
			response.headers().set(LOCATION, forwardUrl);
			response.headers().set(CACHE_CONTROL, NO_CACHE);
			response.headers().set(CONTENT_TYPE, "text/plain");
			response.headers().set(CONTENT_LENGTH,	response.content().readableBytes());
			final int sentBytes = response.content().readableBytes();
			ctx.write(response).addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					ctx.close();
					statusProcessor.fixCloseConnect(forwardUrl, sentBytes);
				}
			});
			
		} else {
			ctx.fireChannelRead(msg);
		}

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
