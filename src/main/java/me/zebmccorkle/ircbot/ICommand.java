package me.zebmccorkle.ircbot;

/**
 * Command - Will not be constructed for each invocation, must be thread-safe
 */
public interface ICommand {
    /**
     * Run the command with the given arguments
     *
     * @param argv List of arguments, it is usually safe to assume they were originally separated by spaces.
     * @return Response, with newlines starting new message
     * @throws Exception Error to be given to user
     */
    String execute(String[] argv, User user) throws Exception;

    /**
     * Detect if a user can use this command
     *
     * @param user User to test
     * @return Whether or not the user can use this command
     */
    boolean userCanUse(User user);
}
