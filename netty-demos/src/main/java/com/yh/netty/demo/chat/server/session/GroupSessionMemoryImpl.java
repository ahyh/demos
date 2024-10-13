package com.yh.netty.demo.chat.server.session;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class GroupSessionMemoryImpl implements GroupSession {

    private static final Map<String, Group> GROUPNAME_GROUP_MAP = new ConcurrentHashMap<>();

    @Override
    public Group createGroup(String groupName, Set<String> members) {
        if (GROUPNAME_GROUP_MAP.get(groupName) != null) {
            // group name existed
            return null;
        }
        Group group = new Group();
        group.setName(groupName);
        group.setMembers(members);
        GROUPNAME_GROUP_MAP.put(groupName, group);
        return group;
    }

    @Override
    public Group joinMember(String groupName, String member) {
        Group group = GROUPNAME_GROUP_MAP.get(groupName);
        if (group == null) {
            return null;
        }
        group.getMembers().add(member);
        return group;
    }

    @Override
    public Group removeMember(String groupName, String member) {
        Group group = GROUPNAME_GROUP_MAP.get(groupName);
        if (group == null) {
            return null;
        }
        group.getMembers().remove(member);
        return group;
    }

    @Override
    public Set<String> getMembers(String groupName) {
        Group group = GROUPNAME_GROUP_MAP.get(groupName);
        if (group == null) {
            return null;
        }
        return group.getMembers();
    }

    @Override
    public List<Channel> getMembersChannel(String groupName) {
        Group group = GROUPNAME_GROUP_MAP.get(groupName);
        if (group == null) {
            return null;
        }
        Session session = SessionFactory.getSession();
        List<Channel> channels = new ArrayList<>();
        Set<String> members = group.getMembers();
        for (String member : members) {
            Channel channel = session.getChannel(member);
            if (channel != null) {
                channels.add(channel);
            }
        }
        return channels;
    }
}
