package com.yh.netty.demo.chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class RpcResponseMessage extends Message{

    private Object returnValue;

    private Exception exceptionValue;

    @Override
    public int getMessageType() {
        return MessageConstants.RpcResponseMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return RpcResponseMessage.class;
    }
}
