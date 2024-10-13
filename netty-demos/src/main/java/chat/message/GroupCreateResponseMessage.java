package chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class GroupCreateResponseMessage extends Message{

    private boolean success;
    private String content;

    public GroupCreateResponseMessage(){}

    public GroupCreateResponseMessage(boolean success, String content) {
        this.success = success;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return MessageConstants.GroupCreateResponseMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return GroupCreateResponseMessage.class;
    }
}
