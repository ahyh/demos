package chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;

public class PongMessage extends Message {


    @Override
    public int getMessageType() {
        return MessageConstants.PongMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return PongMessage.class;
    }
}
