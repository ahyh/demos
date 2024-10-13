package futures;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class NettyFutureTest {

    public static void main(String[] args) throws Exception {
        testWithAsync();
    }

    public static void testWithSync() throws Exception {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(2);

        EventLoop eventLoop = loopGroup.next();

        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                return 50;
            }
        });

        log.info("waiting result");

        Integer result = future.get();

        log.info("result is: {}", result);
    }

    public static void testWithAsync() throws Exception {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(2);
        EventLoop eventLoop = loopGroup.next();
        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                return 50;
            }
        });
        log.info("waiting result");

        // 异步方式获取结果，NIO线程获取到结果
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.info("get result {}", future.getNow());
            }
        });
    }
}
