package alisovets.example.nettysimpletask;

import alisovets.example.nettysimpletask.status.StatusProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;

/**
* A simple HTTP server that sends back the content depending on the received uri
*/
public class SimpleHttpServer {
	private static final int DEFAULT_PORT = 8080;
	public static final AttributeKey<StatusProcessor> STATUS_KEY = new AttributeKey<StatusProcessor>("status");

    private final int port;

    public SimpleHttpServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new SimpleHttpServerInitializer());

            Channel ch = b.bind(port).sync().channel();
            
            System.out.println("Server started");
            ch.closeFuture().sync();            
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = DEFAULT_PORT;
        }
        new SimpleHttpServer(port).run();
    }
}

