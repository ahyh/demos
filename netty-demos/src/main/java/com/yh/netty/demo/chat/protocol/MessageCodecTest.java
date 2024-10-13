package com.yh.netty.demo.chat.protocol;

import com.yh.netty.demo.chat.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

public class MessageCodecTest {

    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                // 解决粘包半包问题的解码器
                new LoggingHandler(),
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                new MessageCodec());

        // 出栈, encode
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("zhangsan", "123456");
        // channel.writeOutbound(loginRequestMessage);

        // 入栈, decode
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        // 将消息填入bytebuf
        new MessageCodec().encode(null, loginRequestMessage, byteBuf);
        // 分两次传输，测试半包问题
        ByteBuf s1 = byteBuf.slice(0, 100);
        ByteBuf s2 = byteBuf.slice(100, byteBuf.readableBytes() - 100);

        // 引用计数+1，writeInbound之后会release，将引用计数-1，为0以后会内存会被释放掉
        s1.retain();
        channel.writeInbound(s1);
        channel.writeInbound(s2);

    }
}
