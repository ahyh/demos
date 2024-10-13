package com.yh.netty.demo.chat.client;

import com.yh.netty.demo.chat.constants.SeqIdGenerator;
import com.yh.netty.demo.chat.message.RpcRequestMessage;
import com.yh.netty.demo.chat.protocol.MessageCodecSharable;
import com.yh.netty.demo.chat.protocol.ProtocolFrameDecoder;
import com.yh.netty.demo.chat.server.handler.RpcResponseMessageHandler;
import com.yh.netty.demo.chat.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

/**
 * Rpc client
 */
@Slf4j
public class RpcClientV2 {

    private static Channel channel = null;
    private static final Object lock = new Object();


    public static void main(String[] args) {
        HelloService helloService = getProxyService(HelloService.class);
        String yanhuan = helloService.hello("Yanhuan");
        System.out.println(yanhuan);
    }

    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (lock) {
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    public static <T> T getProxyService(Class<T> interfaceClass) {
        Object o = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, (proxy, method, args) -> {
            // 将方法调用转换为消息对象
            RpcRequestMessage requestMessage = new RpcRequestMessage(
                    SeqIdGenerator.nextId(),
                    interfaceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args);

            // 将消息发送出去
            getChannel().writeAndFlush(requestMessage);

            // promise传递结果, 指定promise异步接收结果的线程
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.map.put(requestMessage.getSeqId(), promise);

            // 等待promise的结果
            promise.await();

            if (promise.isSuccess()) {
                return promise.getNow();
            } else {
                throw new RuntimeException(promise.cause());
            }
        });
        return (T) o;
    }

    /**
     * 初始化channel
     */
    public static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(loggingHandler);
                ch.pipeline().addLast(messageCodecSharable);
                ch.pipeline().addLast(rpcResponseMessageHandler);
            }
        });
        try {
            channel = bootstrap.connect("127.0.0.1", 8080).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("client error", e);
        }
    }
}
