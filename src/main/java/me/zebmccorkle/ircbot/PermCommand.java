package me.zebmccorkle.ircbot;

import me.zebmccorkle.ircbot.irc.ProtocolUtils;

import java.io.IOException;

@Name("perm")
@Description("Manage permissions")
@Argument(title = "(add|remove)", required = true)
@Argument(title = "permission", required = true)
@Argument(title = "user", required = false)
public class PermCommand implements ICommand {
    @Inject
    private Bot bot;

    private String add(String[] argv, User user) throws User.PermissionException, IOException {
        String username;
        if (argv.length < 4)
            username = user.getName();
        else
            username = ProtocolUtils.toLowercase(argv[3]);
        if (!bot.userRegistered(new User(username)))
            bot.registerUser(new User(username));
        bot.findUser(username).addPermission(argv[2]);
        return "OK!";
    }

    private String remove(String[] argv, User user) throws User.PermissionException {
        String username;
        if (argv.length < 4)
            username = user.getName();
        else
            username = argv[3];
        bot.findUser(username).removePermission(argv[2]);
        return "OK!";
    }

    @Override
    public String execute(String[] argv, User user) throws Exception {
        String command = argv[1];
        switch (command) {
            case "add":
                return add(argv, user);
            case "remove":
                return remove(argv, user);
            default:
                return null;
        }
    }

    @Override
    public boolean userCanUse(User user) {
        return user.hasPermission("op") || user.hasPermission("perm");
    }
}
