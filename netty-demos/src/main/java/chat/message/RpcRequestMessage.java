package chat.message;

import com.yh.netty.demo.chat.constants.MessageConstants;
import lombok.Data;

@Data
public class RpcRequestMessage extends Message {

    private String interfaceName;

    private String methodName;

    private Class<?> returnType;

    private Class[] parameterTypes;

    private Object[] parameterValues;

    public RpcRequestMessage(){}

    public RpcRequestMessage(int seqId, String interfaceName, String methodName, Class<?> returnType,
                             Class[] parameterTypes, Object[] parameterValues) {
        super.setSeqId(seqId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValues = parameterValues;
    }

    @Override
    public int getMessageType() {
        return MessageConstants.RpcRequestMessage;
    }

    @Override
    public Class<?> getMessageClass() {
        return RpcRequestMessage.class;
    }
}
