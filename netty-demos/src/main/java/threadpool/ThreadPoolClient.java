package threadpool;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ThreadPoolClient {

    public static void main(String[] args) throws Exception {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));
        SocketAddress localAddress = sc.getLocalAddress();
        sc.write(Charset.defaultCharset().encode("hello"));
        System.in.read();
    }
}
