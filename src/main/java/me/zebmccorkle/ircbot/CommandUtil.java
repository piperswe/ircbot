package me.zebmccorkle.ircbot;

public class CommandUtil {
    /**
     * Get {@link Argument}s of a command
     * @param command Command to get arguments of
     * @return Array of {@link Argument} annotations present on {@code command.getClass()}
     */
    public static Argument[] getArguments(ICommand command) {
        return command.getClass().getAnnotationsByType(Argument.class);
    }

    /**
     * Check if {@code argc} is enough arguments to execute {@code command}
     * @param command Command to check argument count of
     * @param argc Amount of arguments given
     * @return Whether {@code argc} is enough arguments to execute {@code command}
     */
    public static boolean enoughArguments(ICommand command, int argc) {
        Argument[] arguments = getArguments(command);
        int lastRequiredIndex = 0;
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i].required())
                lastRequiredIndex = i + 1;
        }
        return (argc - 1) >= lastRequiredIndex;
    }

    /**
     * Get the amount of required arguments of a command
     * @param command Command to check required argc of
     * @return Required argc of command
     */
    public static int requiredArguments(ICommand command) {
        Argument[] arguments = getArguments(command);
        int i = 0;
        for (Argument argument : arguments) {
            if (argument.required())
                i++;
        }
        return i;
    }

    /**
     * Get usage string of a command
     * @param command Command to get usage string of
     * @return Usage string of command
     */
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

    /**
     * Get the name of a command
     * @param command Command to get name of
     * @return Name of command
     */
    public static String name(ICommand command) {
        return command.getClass().getAnnotation(Name.class).value();
    }

    /**
     * Get the description of a command
     * @param command Command to get description of
     * @return Description of command
     */
    public static String description(ICommand command) {
        return command.getClass().getAnnotation(Description.class).value();
    }
}
