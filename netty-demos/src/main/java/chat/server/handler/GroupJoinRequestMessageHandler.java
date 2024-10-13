package chat.server.handler;

import com.yh.netty.demo.chat.message.GroupJoinRequestMessage;
import com.yh.netty.demo.chat.message.GroupJoinResponseMessage;
import com.yh.netty.demo.chat.server.session.GroupSession;
import com.yh.netty.demo.chat.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String groupName = msg.getGroupName();

        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Set<String> members = groupSession.getMembers(groupName);
        List<Channel> channels = groupSession.getMembersChannel(groupName);
        if (members.contains(username)) {
            ctx.writeAndFlush(new GroupJoinResponseMessage(false, "you are already in this group"));
        } else {
            groupSession.joinMember(groupName, username);
            for (Channel ch : channels) {
                ch.writeAndFlush(new GroupJoinResponseMessage(true, username + " join group"));
            }
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, "join success"));
        }

    }
}
