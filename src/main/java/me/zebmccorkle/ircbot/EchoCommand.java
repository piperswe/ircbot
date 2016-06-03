package me.zebmccorkle.ircbot;

@Name("echo")
@Description("Echos whatever you say back to you")
@Argument(title = "text", required = true)
public class EchoCommand implements ICommand {
    @Override
    public String execute(String[] arguments, User user) throws Exception {
        String toEcho = "";
        for (int i = 1; i < arguments.length; i++)
            toEcho += " " + arguments[i];
        toEcho = toEcho.substring(1);
        return toEcho;
    }

    @Override
    public boolean userCanUse(User user) {
        return user.hasPermission("op") || user.hasPermission("echo");
    }
}
