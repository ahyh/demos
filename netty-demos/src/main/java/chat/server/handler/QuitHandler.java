package chat.server.handler;

import com.yh.netty.demo.chat.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {

    /**
     * 连接断开的时候触发
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // channel从session中移除
        Channel channel = ctx.channel();
        String username = SessionFactory.getSession().getUsername(ctx.channel());
        SessionFactory.getSession().unbind(channel);
        log.info("user {} leave", username);
    }

    /**
     * 当捕获异常的时候出发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        String username = SessionFactory.getSession().getUsername(ctx.channel());
        SessionFactory.getSession().unbind(channel);
        log.error("user {} exception leave", username);
    }
}
