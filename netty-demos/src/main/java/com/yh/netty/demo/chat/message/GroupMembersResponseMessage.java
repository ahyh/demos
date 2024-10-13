package com.yh.netty.demo.chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;

public class GroupMembersResponseMessage extends Message{
    @Override
    public int getMessageType() {
        return MessageConstants.GroupMembersResponseMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return GroupMembersResponseMessage.class;
    }
}
