package chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class ChatRequestMessage extends Message {

    private String from;
    private String to;
    String content;

    public ChatRequestMessage() {
    }

    public ChatRequestMessage(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return MessageConstants.ChatRequestMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return ChatRequestMessage.class;
    }
}
