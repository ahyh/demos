package chat.server.handler;

import com.yh.netty.demo.chat.message.LoginRequestMessage;
import com.yh.netty.demo.chat.message.LoginResponseMessage;
import com.yh.netty.demo.chat.server.service.UserServiceFactory;
import com.yh.netty.demo.chat.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 无状态，可共享
 */
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        LoginResponseMessage loginResponseMessage;
        if(UserServiceFactory.getUserService().login(username, password)) {
            // 登录成功
            loginResponseMessage = new LoginResponseMessage(true, "success");
            // 保存session
            SessionFactory.getSession().bind(ctx.channel(), username);
        } else {
            loginResponseMessage = new LoginResponseMessage(false, "failed");
        }
        // 返回给客户端login response message
        ctx.writeAndFlush(loginResponseMessage);
    }
}
