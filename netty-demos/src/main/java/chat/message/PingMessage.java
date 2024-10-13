package chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class PingMessage extends Message {


    @Override
    public int getMessageType() {
        return MessageConstants.PingMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return PingMessage.class;
    }
}
