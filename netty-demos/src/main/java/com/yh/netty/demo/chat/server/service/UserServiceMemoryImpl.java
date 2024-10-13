package com.yh.netty.demo.chat.server.service;

import com.yh.netty.demo.chat.constants.UserConstants;

public class UserServiceMemoryImpl implements UserService {

    @Override
    public boolean login(String username, String password) {
        String pwd = UserConstants.USERNAME_PASSWORD_MAP.get(username);
        if (pwd != null && pwd.equals(password)) {
            return true;
        }
        return false;
    }
}
