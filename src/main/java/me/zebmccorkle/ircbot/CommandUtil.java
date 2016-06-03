package me.zebmccorkle.ircbot;

public class CommandUtil {
    public static Argument[] getArguments(ICommand command) {
        return command.getClass().getAnnotationsByType(Argument.class);
    }

    public static boolean enoughArguments(ICommand command, int argc) {
        Argument[] arguments = getArguments(command);
        int lastRequiredIndex = 0;
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i].required())
                lastRequiredIndex = i + 1;
        }
        return (argc - 1) >= lastRequiredIndex;
    }

    public static int requiredArguments(ICommand command) {
        Argument[] arguments = getArguments(command);
        int i = 0;
        for (Argument argument : arguments) {
            if (argument.required())
                i++;
        }
        return i;
    }

    public static String usage(ICommand command) {
        try {
            Argument[] arguments = command.getClass().getAnnotationsByType(Argument.class);
            String usage = "";
            for (Object argumentobj : arguments) {
                if (!(argumentobj instanceof Argument))
                    continue;
                Argument argument = (Argument) argumentobj;
                char leftBracket = argument.required() ? '<' : '[';
                char rightBracket = argument.required() ? '>' : ']';
                usage += String.format("%c%s%c ", leftBracket, argument.title(), rightBracket);
            }
            return usage;
        } catch (NullPointerException e) {
            return " ";
        }
    }

    public static String name(ICommand command) {
        return command.getClass().getAnnotation(Name.class).value();
    }

    public static String description(ICommand command) {
        return command.getClass().getAnnotation(Description.class).value();
    }
}
