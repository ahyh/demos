package chat.server.handler;

import com.yh.netty.demo.chat.constants.ServiceFactory;
import com.yh.netty.demo.chat.message.RpcRequestMessage;
import com.yh.netty.demo.chat.message.RpcResponseMessage;
import com.yh.netty.demo.chat.server.service.HelloService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 处理rpc消息的Handler
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage rpcRequestMessage) throws Exception {
        try {
            HelloService helloService = (HelloService) ServiceFactory.getService(rpcRequestMessage.getInterfaceName());
            Method method = helloService.getClass().getMethod(rpcRequestMessage.getMethodName(), rpcRequestMessage.getParameterTypes());
            Object invoke = method.invoke(helloService, rpcRequestMessage.getParameterValues());
            RpcResponseMessage responseMessage = new RpcResponseMessage();
            responseMessage.setSeqId(rpcRequestMessage.getSeqId());
            responseMessage.setReturnValue(invoke);
            ctx.writeAndFlush(responseMessage);
        } catch (Exception e) {
            log.error("rpc execute error", e);
            RpcResponseMessage responseMessage = new RpcResponseMessage();
            responseMessage.setSeqId(rpcRequestMessage.getSeqId());
            responseMessage.setExceptionValue(new Exception("remote invoke error: " + e.getCause().getMessage()));
            ctx.writeAndFlush(responseMessage);
        }
    }

}
