package chat.protocol;

import com.yh.netty.demo.chat.message.Message;
import com.yh.netty.demo.chat.protocol.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * 编解码器
 * 必须和LengthFieldBasedFrameDecoder一起使用，确保没有粘包半包问题
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 1- 写入4个字节的魔数
        out.writeBytes(new byte[]{8, 8, 8, 8});
        // 2- 写入一个字节的版本号
        out.writeByte(1);
        // 3- 写入一个字节的序列化方式, 1- JDK
        out.writeByte(1);
        // 4- 写入一个字节的MessageType
        out.writeByte(msg.getMessageType());
        // 5- 写入4个字节的seqId
        out.writeInt(msg.getSeqId());
        // 对齐填充用
        out.writeByte(0xff);
        // 6- 写入消息正文, 先写入消息长度，在写入消息内容
        byte[] bytes = Serializer.Algorithm.JDK.serialize(msg);
        // 6.1 - 写入长度
        out.writeInt(bytes.length);
        // 6.2 - 写入消息
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNumber = in.readInt();
        byte version = in.readByte();
        byte serializeType = in.readByte();
        byte messageType = in.readByte();
        int seqId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        // 反序列化
        Message msg = Serializer.Algorithm.JDK.deserialize(Message.class, bytes);
        log.debug("{},{},{},{},{},{}", magicNumber, version, serializeType, messageType, seqId, length);
        log.debug("message {}", msg);
        out.add(msg);
    }
}
