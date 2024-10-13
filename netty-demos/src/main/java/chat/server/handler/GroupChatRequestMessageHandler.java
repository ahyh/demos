package chat.server.handler;

import com.yh.netty.demo.chat.message.GroupChatRequestMessage;
import com.yh.netty.demo.chat.message.GroupChatResponseMessage;
import com.yh.netty.demo.chat.server.session.GroupSessionFactory;
import com.yh.netty.demo.chat.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String from = msg.getFrom();
        String groupName = msg.getGroupName();
        Channel channel = SessionFactory.getSession().getChannel(from);
        List<Channel> channels = GroupSessionFactory.getGroupSession().getMembersChannel(groupName);
        if (channels == null) {
            ctx.writeAndFlush(new GroupChatResponseMessage(false, "group not existed"));
        } else {
            for (Channel ch : channels) {
                if (ch != channel) {
                    // 不需要给发送者在发送消息
                    ch.writeAndFlush(new GroupChatResponseMessage(from, msg.getContent()));
                }
            }
        }
    }
}
