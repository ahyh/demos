package com.yh.netty.demo.chat.server.session;

import lombok.Data;

import java.util.Set;

@Data
public class Group {

    private String name;
    private Set<String> members;

}
