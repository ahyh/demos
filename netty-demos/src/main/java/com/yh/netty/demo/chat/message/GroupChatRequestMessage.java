package com.yh.netty.demo.chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class GroupChatRequestMessage extends Message {

    private String from;
    private String groupName;
    private String content;

    public GroupChatRequestMessage() {
    }

    public GroupChatRequestMessage(String from, String groupName, String content) {
        this.from = from;
        this.groupName = groupName;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return MessageConstants.GroupChatRequestMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return GroupChatRequestMessage.class;
    }
}
