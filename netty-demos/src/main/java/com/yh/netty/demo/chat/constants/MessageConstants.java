package com.yh.netty.demo.chat.constants;

import com.yh.netty.demo.chat.message.*;

import java.util.HashMap;
import java.util.Map;

public class MessageConstants {

    public static final int LoginRequestMessage = 0;
    public static final int LoginResponseMessage = 1;
    public static final int ChatRequestMessage = 2;
    public static final int ChatResponseMessage = 3;
    public static final int GroupCreateRequestMessage = 4;
    public static final int GroupCreateResponseMessage = 5;
    public static final int GroupJoinRequestMessage = 6;
    public static final int GroupJoinResponseMessage = 7;

    public static final int GroupQuitRequestMessage = 8;
    public static final int GroupQuitResponseMessage = 9;

    public static final int GroupChatRequestMessage = 10;
    public static final int GroupChatResponseMessage = 11;

    public static final int GroupMembersRequestMessage = 12;
    public static final int GroupMembersResponseMessage = 13;

    public static final int PingMessage = 14;
    public static final int PongMessage = 15;

    public static final int RpcRequestMessage = 101;
    public static final int RpcResponseMessage = 102;

    public static final Map<Integer, Class<?>> MESSAGE_CLASS_MAP = new HashMap<>();

    static {
        MESSAGE_CLASS_MAP.put(LoginRequestMessage, LoginRequestMessage.class);
        MESSAGE_CLASS_MAP.put(LoginResponseMessage, LoginResponseMessage.class);
        MESSAGE_CLASS_MAP.put(ChatRequestMessage, ChatRequestMessage.class);
        MESSAGE_CLASS_MAP.put(ChatResponseMessage, ChatResponseMessage.class);
        MESSAGE_CLASS_MAP.put(GroupCreateRequestMessage, GroupCreateRequestMessage.class);
        MESSAGE_CLASS_MAP.put(GroupCreateResponseMessage, GroupCreateResponseMessage.class);
        MESSAGE_CLASS_MAP.put(GroupJoinRequestMessage, GroupJoinRequestMessage.class);
        MESSAGE_CLASS_MAP.put(GroupJoinResponseMessage, GroupJoinResponseMessage.class);
        MESSAGE_CLASS_MAP.put(GroupQuitRequestMessage, GroupQuitRequestMessage.class);
        MESSAGE_CLASS_MAP.put(GroupQuitResponseMessage, GroupQuitResponseMessage.class);
        MESSAGE_CLASS_MAP.put(GroupChatRequestMessage, GroupChatRequestMessage.class);
        MESSAGE_CLASS_MAP.put(GroupChatResponseMessage, GroupChatResponseMessage.class);
        MESSAGE_CLASS_MAP.put(GroupMembersRequestMessage, GroupMembersRequestMessage.class);
        MESSAGE_CLASS_MAP.put(GroupMembersResponseMessage, GroupMembersResponseMessage.class);
        MESSAGE_CLASS_MAP.put(RpcRequestMessage, RpcRequestMessage.class);
        MESSAGE_CLASS_MAP.put(RpcResponseMessage, RpcResponseMessage.class);
    }
}
