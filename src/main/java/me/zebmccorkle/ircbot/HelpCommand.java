package me.zebmccorkle.ircbot;

import java.util.Map;

import static me.zebmccorkle.ircbot.CommandUtil.usage;

@Name("help")
@Description("Show this message")
public class HelpCommand implements ICommand {
    @Inject
    private Bot bot;

    @Override
    public String execute(String[] arguments, User user) throws Exception {
        String help = "Available commands:";
        for (Map.Entry<String, ICommand> command : bot.commands.entrySet()) {
            if (command.getValue().userCanUse(user))
                help += "\n" +
                        bot.commandCharacter +
                        command.getKey() +
                        " " +
                        usage(command.getValue()) +
                        "- " +
                        CommandUtil.description(command.getValue());
        }
        return help;
    }

    @Override
    public boolean userCanUse(User user) {
        return true;
    }
}
