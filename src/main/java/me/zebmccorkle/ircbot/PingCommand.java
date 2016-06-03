package me.zebmccorkle.ircbot;

@Name("ping")
@Description("Print \"Pong!\"")
public class PingCommand implements ICommand {
    @Override
    public String execute(String[] arguments, User user) throws Exception {
        return "Pong!";
    }

    @Override
    public boolean userCanUse(User user) {
        return user.hasPermission("op") || user.hasPermission("ping");
    }
}
