package org.example.netty.playing;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.example.netty.handler.example.ByteBufToStringInboundHandler;
import org.example.netty.handler.example.MainStringInboundHandler;
import org.example.netty.handler.example.MainStringOutboundHandler;
import org.example.netty.handler.example.StringToByteBufOutboundHandler;

@Slf4j
public class NettyCorePipelineServer {
    public static void main(String[] args) {
        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ByteBufToStringInboundHandler(),
                                    new StringToByteBufOutboundHandler(),
                                    new MainStringOutboundHandler(),
                                    new MainStringInboundHandler()

                            );
                        }
                    });
            ChannelFuture future = server.bind(8189).sync();
            log.debug("Server was ready...");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
