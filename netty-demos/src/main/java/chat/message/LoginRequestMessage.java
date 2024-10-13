package chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class LoginRequestMessage extends Message {

    private String username;
    private String password;

    public LoginRequestMessage() {

    }

    public LoginRequestMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public int getMessageType() {
        return MessageConstants.LoginRequestMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return LoginRequestMessage.class;
    }
}
