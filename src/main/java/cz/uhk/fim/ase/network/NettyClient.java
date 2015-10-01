package cz.uhk.fim.ase.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author Tomáš Kolinger <tomas@kolinger.name>
 */
public class NettyClient implements Runnable {

    private String address;
    private int port;
    private boolean running = true;
    private int count = 1;

    public NettyClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        try {
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new ObjectEncoder(),
                                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                new Handler());
                    }
                });
                ChannelFuture future = bootstrap.connect(address, port).sync();
                future.channel().closeFuture().sync();
            } finally {
                workerGroup.shutdownGracefully();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Handler extends ChannelHandlerAdapter {

        @Override
        public void channelActive(final ChannelHandlerContext context) {
            send(context);
        }

        public void send(final ChannelHandlerContext context) {
            try {
                Message message = new Message();
                message.setContent(Message.DEFAULT_CONTENT);
                ChannelFuture future = context.writeAndFlush(message);
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) {
                        if (getCount() < Benchmark.MESSAGES_COUNT) {
                            send(context);
                        } else {
                            running = false;
                            future.channel().close();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private synchronized int getCount() {
            return count++;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
            cause.printStackTrace();
            context.close();
        }
    }
}
