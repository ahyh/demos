package com.yh.netty.demo.chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class GroupQuitRequestMessage extends Message{

    private String username;
    private String groupName;

    public GroupQuitRequestMessage() {}

    public GroupQuitRequestMessage(String username, String groupName) {
        this.username = username;
        this.groupName = groupName;
    }

    @Override
    public int getMessageType() {
        return MessageConstants.GroupQuitRequestMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return GroupQuitRequestMessage.class;
    }
}
