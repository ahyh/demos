package chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class LoginResponseMessage extends Message {

    private boolean success;
    private String reason;

    public LoginResponseMessage(){}

    public LoginResponseMessage(boolean success, String reason){
        this.success = success;
        this.reason = reason;
    }
    @Override
    public int getMessageType() {
        return MessageConstants.LoginResponseMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return LoginResponseMessage.class;
    }
}
