package chat.server.handler;

import com.yh.netty.demo.chat.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    public static final Map<Integer, Promise<Object>> map = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.info("{}", msg);
        // 拿到promise
        Promise<Object> promise = map.remove(msg.getSeqId());
        if (promise != null) {
            if (msg.getReturnValue() != null) {
                promise.setSuccess(msg.getReturnValue());
            } else if (msg.getExceptionValue() != null) {
                promise.setFailure(msg.getExceptionValue());
            }
        }
    }



}
