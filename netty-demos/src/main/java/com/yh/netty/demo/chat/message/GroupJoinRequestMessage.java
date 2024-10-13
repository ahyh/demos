package com.yh.netty.demo.chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class GroupJoinRequestMessage extends Message {

    private String username;
    private String groupName;

    public GroupJoinRequestMessage() {}

    public GroupJoinRequestMessage(String username, String groupName) {
        this.username = username;
        this.groupName = groupName;
    }

    @Override
    public int getMessageType() {
        return MessageConstants.GroupJoinRequestMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return GroupJoinRequestMessage.class;
    }
}
