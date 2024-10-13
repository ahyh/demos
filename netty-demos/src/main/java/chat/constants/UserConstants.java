package chat.constants;

import java.util.HashMap;
import java.util.Map;

public class UserConstants {

    public static final Map<String, String> USERNAME_PASSWORD_MAP = new HashMap<>();

    static {
        USERNAME_PASSWORD_MAP.put("zhangsan", "123");
        USERNAME_PASSWORD_MAP.put("lisi", "123");
        USERNAME_PASSWORD_MAP.put("wangwu", "123");
        USERNAME_PASSWORD_MAP.put("alice", "123");
        USERNAME_PASSWORD_MAP.put("bob", "123");
        USERNAME_PASSWORD_MAP.put("cake", "123");
        USERNAME_PASSWORD_MAP.put("dog", "123");
        USERNAME_PASSWORD_MAP.put("egg", "123");
    }
}
