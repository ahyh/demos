package chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class GroupQuitResponseMessage extends Message {

    private boolean success;
    private String content;

    public GroupQuitResponseMessage() {
    }

    public GroupQuitResponseMessage(boolean success, String content) {
        this.success = success;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return MessageConstants.GroupQuitResponseMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return GroupQuitResponseMessage.class;
    }
}
