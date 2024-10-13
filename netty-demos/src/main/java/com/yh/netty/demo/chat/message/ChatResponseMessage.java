package com.yh.netty.demo.chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class ChatResponseMessage extends Message {

    /**
     * 消息的发送者
     */
    private String from;

    /**
     * 消息的内容
     */
    private String content;

    private boolean success;

    public ChatResponseMessage() {
    }

    public ChatResponseMessage(String from, String content) {
        this.from = from;
        this.content = content;
        this.success = true;
    }

    public ChatResponseMessage(boolean success, String content) {
        this.success = success;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return MessageConstants.ChatResponseMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return ChatResponseMessage.class;
    }
}
