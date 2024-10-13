import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class ChannelTest {

    public static void main(String[] args) {
        try (FileChannel channel = new FileInputStream("D:\\myCode\\repos2023\\files\\welcome.html").getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(32);
            while (true) {
                int len = channel.read(buffer);
                if (len == -1) {
                    break;
                }
                buffer.flip();
                log.debug("read content:{}", new String(buffer.array(), 0, buffer.remaining()));
                buffer.clear();
            }
        } catch (Exception e) {
            log.error("channel test error:{}", e.getMessage());
        }
    }
}
