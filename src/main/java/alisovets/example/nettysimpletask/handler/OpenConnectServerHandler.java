package alisovets.example.nettysimpletask.handler;


import alisovets.example.nettysimpletask.SimpleHttpServer;
import alisovets.example.nettysimpletask.status.StatusProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;

/**
 * Handles a server-side channel.
 */
public class OpenConnectServerHandler extends ChannelInboundHandlerAdapter {
	private StatusProcessor statusProcessor;
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

    	ByteBuf in = (ByteBuf) msg;
    	int msgSize = in.readableBytes();
    	
    	statusProcessor = new StatusProcessor();
    	statusProcessor.fixOpenConnect(msgSize);
    	
    	Attribute<StatusProcessor> attribute = ctx.channel().attr(SimpleHttpServer.STATUS_KEY);
    	attribute.set(statusProcessor);
    	
    	ctx.fireChannelRead(msg);   
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
        if(statusProcessor != null){
			statusProcessor.fixCloseConnect(null, 0);
		}
    }
}