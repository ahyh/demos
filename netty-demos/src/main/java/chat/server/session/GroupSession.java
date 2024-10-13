package chat.server.session;


import io.netty.channel.Channel;

import java.util.List;
import java.util.Set;

public interface GroupSession {

    Group createGroup(String groupName, Set<String> members);

    Group joinMember(String groupName, String member);

    Group removeMember(String groupName, String member);

    Set<String> getMembers(String groupName);

    List<Channel> getMembersChannel(String groupName);


}
