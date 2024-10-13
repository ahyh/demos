package chat.server.session;

public class GroupSessionFactory {

    private static final GroupSession groupSession = new GroupSessionMemoryImpl();

    public static GroupSession getGroupSession() {
        return groupSession;
    }
}
