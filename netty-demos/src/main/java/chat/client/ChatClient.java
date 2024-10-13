package chat.client;

import com.yh.netty.demo.chat.message.*;
import com.yh.netty.demo.chat.protocol.MessageCodecSharable;
import com.yh.netty.demo.chat.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {

    public static void main(String[] args) {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(loopGroup);

            // 配置参数, 连接超时时间
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);

            LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
            MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
            CountDownLatch countDownLatch = new CountDownLatch(1);
            AtomicBoolean loginStatus = new AtomicBoolean(false);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(loggingHandler);
                    ch.pipeline().addLast(messageCodecSharable);

                    // 如果3s内没有写数据，触发write idle事件
                    ch.pipeline().addLast(new IdleStateHandler(0,3,0));

                    ch.pipeline().addLast(new ChannelDuplexHandler(){

                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            if (event.state() == IdleState.WRITER_IDLE) {
                                // 3s没写数据，发送一个心跳包
                                ctx.writeAndFlush(new PingMessage());
                            }
                            super.userEventTriggered(ctx, evt);
                        }
                    });

                    ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {

                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("connect closed");
                            countDownLatch.countDown();
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            System.out.println("connect exception closed");
                            countDownLatch.countDown();
                        }

                        /**
                         * 连接建立后触发channel active事件
                         */
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            new Thread(() -> {
                                // 创建新的线程，向服务器发送消息
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("please input username: ");
                                String username = scanner.nextLine();
                                System.out.println("please input password: ");
                                String password = scanner.nextLine();
                                // 发送消息
                                LoginRequestMessage loginRequestMessage = new LoginRequestMessage(username, password);
                                ctx.writeAndFlush(loginRequestMessage);

                                // 阻塞住等待响应
                                try {
                                    countDownLatch.await();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                if (!loginStatus.get()) {
                                    // 登录失败
                                    ctx.channel().close();
                                    return;
                                }
                                while (true) {
                                    // 展示菜单
                                    System.out.println("============================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("============================================");

                                    String cmd = scanner.nextLine();
                                    String[] s = cmd.split(" ");
                                    switch (s[0]) {
                                        case "send":
                                            ChatRequestMessage chatRequestMessage = new ChatRequestMessage(username, s[1], s[2]);
                                            // 发送一条chat message
                                            ctx.writeAndFlush(chatRequestMessage);
                                            break;
                                        case "gsend":
                                            GroupChatRequestMessage groupChatRequestMessage = new GroupChatRequestMessage(username, s[1], s[2]);
                                            ctx.writeAndFlush(groupChatRequestMessage);
                                            break;
                                        case "gcreate":
                                            Set<String> members = new HashSet<>(Arrays.asList(s[2].split(",")));
                                            // 将建群的人自己也拉入群
                                            members.add(username);
                                            GroupCreateRequestMessage groupCreateRequestMessage = new GroupCreateRequestMessage(s[1], members);
                                            ctx.writeAndFlush(groupCreateRequestMessage);
                                            break;
                                        case "gmembers":
                                            GroupMembersRequestMessage groupMembersRequestMessage = new GroupMembersRequestMessage(s[1]);
                                            ctx.writeAndFlush(groupMembersRequestMessage);
                                            break;
                                        case "gjoin":
                                            GroupJoinRequestMessage groupJoinRequestMessage = new GroupJoinRequestMessage(username, s[1]);
                                            ctx.writeAndFlush(groupJoinRequestMessage);
                                            break;
                                        case "gquit":
                                            GroupQuitRequestMessage groupQuitRequestMessage = new GroupQuitRequestMessage(username, s[1]);
                                            ctx.writeAndFlush(groupQuitRequestMessage);
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;

                                    }
                                }
                            }, "system in").start();
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            if (msg instanceof LoginResponseMessage) {
                                log.debug("{}", msg);
                                LoginResponseMessage loginResponseMessage = (LoginResponseMessage) msg;
                                if (loginResponseMessage.isSuccess()) {
                                    // 登录成功
                                    loginStatus.set(true);
                                }
                            }
                            // 响应回来后，count down latch减1，唤醒await
                            countDownLatch.countDown();
                        }
                    });
                }

            });
            Channel channel = bootstrap.connect("127.0.0.1", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            loopGroup.shutdownGracefully();
        }
    }
}
