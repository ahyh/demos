package bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class TestSlice {

    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(new byte[]{'1','2','3','4','5','6','7','8','9','a'});
        System.out.println(buffer);
        ByteBuf f1 = buffer.slice(0, 5);
        ByteBuf f2 = buffer.slice(0, 10);

        f1.setByte(0,'b');
        System.out.println(f1);
        System.out.println(f2);
    }
}
