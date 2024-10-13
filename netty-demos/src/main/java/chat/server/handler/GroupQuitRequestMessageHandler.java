package chat.server.handler;

import com.yh.netty.demo.chat.message.GroupJoinResponseMessage;
import com.yh.netty.demo.chat.message.GroupQuitRequestMessage;
import com.yh.netty.demo.chat.message.GroupQuitResponseMessage;
import com.yh.netty.demo.chat.server.session.GroupSession;
import com.yh.netty.demo.chat.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

@ChannelHandler.Sharable
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String groupName = msg.getGroupName();

        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Set<String> members = groupSession.getMembers(groupName);
        List<Channel> channels = groupSession.getMembersChannel(groupName);

        if (members.contains(username)) {
            groupSession.removeMember(groupName, username);
            for (Channel ch : channels) {
                if (ch != ctx.channel()) {
                    ch.writeAndFlush(new GroupQuitResponseMessage(true, username + " quit group"));
                } else {
                    ctx.writeAndFlush(new GroupQuitResponseMessage(true, "you are quit " + groupName));
                }
            }
        } else {
            ctx.writeAndFlush(new GroupQuitResponseMessage(false, "you are not in " + groupName));
        }
    }
}
