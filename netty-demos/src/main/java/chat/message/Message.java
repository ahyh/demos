package chat.message;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class Message implements Serializable {

    private int seqId;
    private int messageType;

    public abstract int getMessageType();

    public abstract Class<?> getMessageClass();


}
