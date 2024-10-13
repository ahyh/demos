package com.yh.netty.demo.redis;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * redis客户端
 */
@Slf4j
public class RedisTest {

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ByteBuf buf = ctx.alloc().buffer();
                            buf.writeBytes("*3".getBytes());
                            buf.writeBytes(new byte[]{13, 10});
                            buf.writeBytes("$3".getBytes());
                            buf.writeBytes(new byte[]{13, 10});
                            buf.writeBytes("set".getBytes());
                            buf.writeBytes(new byte[]{13, 10});
                            buf.writeBytes("$6".getBytes());
                            buf.writeBytes(new byte[]{13, 10});
                            buf.writeBytes("harvey".getBytes());
                            buf.writeBytes(new byte[]{13, 10});
                            buf.writeBytes("$3".getBytes());
                            buf.writeBytes(new byte[]{13, 10});
                            buf.writeBytes("yan".getBytes());
                            buf.writeBytes(new byte[]{13, 10});
                            ctx.writeAndFlush(buf);
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf = (ByteBuf) msg;
                            System.out.println(buf.toString(Charset.defaultCharset()));
                        }
                    });
                }
            });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6379).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("com.yh.netty.demo.redis client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
