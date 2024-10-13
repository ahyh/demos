package chat.server.handler;

import com.yh.netty.demo.chat.message.GroupCreateRequestMessage;
import com.yh.netty.demo.chat.message.GroupCreateResponseMessage;
import com.yh.netty.demo.chat.server.session.Group;
import com.yh.netty.demo.chat.server.session.GroupSession;
import com.yh.netty.demo.chat.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (group != null) {
            // 给member发送消息
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            if (channels != null) {
                channels.forEach(channel -> {
                    channel.writeAndFlush(new GroupCreateResponseMessage(true, "you are invited into " + groupName));
                });
            }

            // 创建成功给创建者发送消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true, "create success"));
        } else {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, "create failed: " + groupName + "existed"));
        }

    }
}
