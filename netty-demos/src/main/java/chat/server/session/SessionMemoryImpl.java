package chat.server.session;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存实现的Session
 */
@Slf4j
public class SessionMemoryImpl implements Session{

    private static final Map<String, Channel> USERNAME_CHANNEL_MAP = new ConcurrentHashMap<>();
    private static final Map<Channel, String> CHANNEL_USERNAME_MAP = new ConcurrentHashMap<>();
    private static final Map<Channel, Map<String, Object>> CHANNEL_ATTRIBUTES_MAP = new ConcurrentHashMap<>();
    @Override
    public void bind(Channel channel, String username) {
        if (channel == null || username == null) {
            return;
        }
        USERNAME_CHANNEL_MAP.put(username, channel);
        CHANNEL_USERNAME_MAP.put(channel, username);
        CHANNEL_ATTRIBUTES_MAP.put(channel, new ConcurrentHashMap<>());
    }

    @Override
    public void unbind(Channel channel) {
        String username = CHANNEL_USERNAME_MAP.get(channel);
        if (username == null) {
            return;
        }
        CHANNEL_USERNAME_MAP.remove(channel);
        USERNAME_CHANNEL_MAP.remove(username);
        CHANNEL_ATTRIBUTES_MAP.remove(channel);
    }

    @Override
    public Object getAttribute(Channel channel, String name) {
        if (channel == null || name == null) {
            return null;
        }
        Map<String, Object> name2AttrMap = CHANNEL_ATTRIBUTES_MAP.get(channel);
        return name2AttrMap.get(name);
    }

    @Override
    public void setAttribute(Channel channel, String name, Object value) {
        if (channel == null || name == null || value == null) {
            return;
        }
        Map<String, Object> name2AttrMap = CHANNEL_ATTRIBUTES_MAP.get(channel);
        name2AttrMap.put(name, value);
    }

    @Override
    public Channel getChannel(String username) {
        return USERNAME_CHANNEL_MAP.get(username);
    }

    @Override
    public String getUsername(Channel channel) {
        return CHANNEL_USERNAME_MAP.get(channel);
    }
}
