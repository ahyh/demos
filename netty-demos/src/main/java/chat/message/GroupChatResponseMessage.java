package chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class GroupChatResponseMessage extends Message {

    private boolean success;

    private String from;

    private String content;

    public GroupChatResponseMessage() {
    }

    public GroupChatResponseMessage(String from, String content) {
        this.from = from;
        this.content = content;
        this.success = true;
    }


    public GroupChatResponseMessage(boolean success, String content) {
        this.success = success;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return MessageConstants.GroupChatResponseMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return GroupChatResponseMessage.class;
    }
}
