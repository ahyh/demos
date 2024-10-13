import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public class ByteBufferTest {

    /**
     * 粘包和拆包
     */
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put("Hello,world\nI'm ZhangSan\nHo".getBytes());
        split(buffer);
        buffer.put("w are you?\n".getBytes());
        split(buffer);
    }

    private static void split(ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            // 读取到一条完整的消息
            if (buffer.get(i) == '\n') {
                // 把完整的消息存入新的ByteBuffer
                int len = i + 1 - buffer.position();
                ByteBuffer target = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    target.put(buffer.get());
                }
                target.flip();
                log.debug(new String(target.array(), 0, target.remaining()));
            }
        }
        // 未读的部分向前移动，等待和下次读取的内容合并
        buffer.compact();
    }
}
