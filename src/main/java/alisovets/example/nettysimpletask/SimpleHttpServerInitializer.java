package alisovets.example.nettysimpletask;

import alisovets.example.nettysimpletask.handler.Http404ServerHandler;
import alisovets.example.nettysimpletask.handler.HttpHelloServerHandler;
import alisovets.example.nettysimpletask.handler.HttpRedirectServerHandler;
import alisovets.example.nettysimpletask.handler.HttpStatusServerHandler;
import alisovets.example.nettysimpletask.handler.OpenConnectServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class SimpleHttpServerInitializer extends ChannelInitializer<SocketChannel> {
	
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline p = ch.pipeline();
        
        p.addLast("open", new OpenConnectServerHandler());
        p.addLast("decoder", new HttpRequestDecoder());
        p.addLast("encoder", new HttpResponseEncoder());
        p.addLast("hello", new HttpHelloServerHandler());
        p.addLast("forward", new HttpRedirectServerHandler());
        p.addLast("status", new HttpStatusServerHandler());
        p.addLast("404", new Http404ServerHandler());
        
    }
}