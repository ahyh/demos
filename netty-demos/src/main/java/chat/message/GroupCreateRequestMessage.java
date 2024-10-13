package chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

import java.util.Set;

@Data
public class GroupCreateRequestMessage extends Message {

    private String groupName;

    private Set<String> members;

    public GroupCreateRequestMessage(){}

    public GroupCreateRequestMessage(String groupName, Set<String> members){
        this.groupName = groupName;
        this.members = members;
    }

    @Override
    public int getMessageType() {
        return MessageConstants.GroupCreateRequestMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return GroupCreateRequestMessage.class;
    }
}
