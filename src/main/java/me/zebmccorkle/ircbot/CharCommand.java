package me.zebmccorkle.ircbot;

@Name("char")
@Description("Set the command prefix character")
@Argument(title = "character", required = true)
public class CharCommand implements ICommand {
    @Inject
    private Bot bot;

    @Override
    public String execute(String[] argv, User user) throws Exception {
        bot.commandCharacter = argv[1].charAt(0);
        return "OK!";
    }

    @Override
    public boolean userCanUse(User user) {
        return user.hasPermission("op");
    }
}
