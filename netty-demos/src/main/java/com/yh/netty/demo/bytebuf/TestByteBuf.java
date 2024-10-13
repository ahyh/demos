package com.yh.netty.demo.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * ByteBuf可以自动扩缩容
 */
public class TestByteBuf {

    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        System.out.println(buffer);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 300; i++) {
            sb.append("a");
        }

        buffer.writeBytes(sb.toString().getBytes());
        System.out.println(buffer);
    }
}
