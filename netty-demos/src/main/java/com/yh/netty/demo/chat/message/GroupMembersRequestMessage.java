package com.yh.netty.demo.chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class GroupMembersRequestMessage extends Message {

    private String groupName;

    public GroupMembersRequestMessage() {}

    public GroupMembersRequestMessage(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public int getMessageType() {
        return MessageConstants.GroupMembersRequestMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return GroupMembersRequestMessage.class;
    }
}
