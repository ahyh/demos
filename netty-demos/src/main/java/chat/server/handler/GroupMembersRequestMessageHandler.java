package chat.server.handler;

import com.yh.netty.demo.chat.message.GroupMembersRequestMessage;
import com.yh.netty.demo.chat.server.session.GroupSession;
import com.yh.netty.demo.chat.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@ChannelHandler.Sharable
public class GroupMembersRequestMessageHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();

        Set<String> members = groupSession.getMembers(groupName);
    }
}
