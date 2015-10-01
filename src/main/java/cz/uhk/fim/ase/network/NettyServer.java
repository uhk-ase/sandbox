package cz.uhk.fim.ase.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author Tomáš Kolinger <tomas@kolinger.name>
 */
public class NettyServer implements Runnable {

    private String address;
    private int port;
    private int success = 0;
    private int corrupted = 0;
    private long startTime;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public NettyServer(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new Handler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(address, port).sync();
            startTime = System.currentTimeMillis();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void print() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();

        double duration = (System.currentTimeMillis() - startTime) / (double) 1000; // second
        double rate = success / duration;
        System.out.println("Netty -> success: " + success + " / corrupted: " + corrupted + " / rate: " + rate);
    }

    public class Handler extends ChannelHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext context, Object msg) {
            try {
                Message message = (Message) msg;
                if (message == null || !message.getContent().equals(Message.DEFAULT_CONTENT)) {
                    corrupted++;
                } else {
                    success++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
            cause.printStackTrace();
            context.close();
        }
    }
}
