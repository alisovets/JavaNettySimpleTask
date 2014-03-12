package alisovets.example.nettysimpletask.handler;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.Attribute;

import java.net.InetSocketAddress;

import alisovets.example.nettysimpletask.SimpleHttpServer;
import alisovets.example.nettysimpletask.status.StatusProcessor;

/**
 * an http server handler that sends 'Hello' after 10 seconds if the request
 * containing the uri with the 'hello' path
 */
public class HttpHelloServerHandler extends ChannelInboundHandlerAdapter {
	private final static String HELLO_URI_PATH = "/hello";
	private static final byte[] CONTENT = "Hello, World".getBytes();
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
		Attribute<StatusProcessor> attribute = ctx.channel().attr(
				SimpleHttpServer.STATUS_KEY);
		statusProcessor = attribute.get();

		HttpRequest req = (HttpRequest) msg;

		String uri = req.getUri();
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
		if (!queryStringDecoder.path().equals(HELLO_URI_PATH)) {
			ctx.fireChannelRead(msg);
			return;
		}

		String ip = ((InetSocketAddress) ctx.channel().remoteAddress())
				.getAddress().getHostAddress();
		statusProcessor.fixHttpOpenConnect(ip, uri);

		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
				OK, Unpooled.wrappedBuffer(CONTENT));
		response.headers().set(CONTENT_TYPE, "text/plain");
		response.headers().set(CONTENT_LENGTH,
				response.content().readableBytes());
		
		final int sentBytes = response.content().readableBytes();
		Thread.sleep(10000);
		ctx.write(response).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
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
		if (statusProcessor != null) {
			statusProcessor.fixCloseConnect(null, 0);
		}
	}
}
