package com.yh.netty.demo.chat.constants;

import com.yh.netty.demo.chat.server.service.HelloServiceImpl;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactory {

    public static final Map<String, Object> SERVICE_MAP = new HashMap<>();

    static {
        SERVICE_MAP.put("com.yh.netty.demo.chat.server.service.HelloService", new HelloServiceImpl());
    }

    public static Object getService(String interfaceName) {
        return SERVICE_MAP.get(interfaceName);
    }


}
