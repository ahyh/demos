package chat.server.service;

public class UserServiceFactory {

    private static final UserService userService = new UserServiceMemoryImpl();

    public static UserService getUserService() {
        return userService;
    }
}
