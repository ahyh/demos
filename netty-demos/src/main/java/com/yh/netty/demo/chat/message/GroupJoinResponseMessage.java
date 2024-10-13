package com.yh.netty.demo.chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class GroupJoinResponseMessage extends Message{

    private boolean success;
    private String content;

    public GroupJoinResponseMessage(){}

    public GroupJoinResponseMessage(boolean success, String content) {
        this.success = success;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return MessageConstants.GroupJoinResponseMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return GroupJoinResponseMessage.class;
    }
}
