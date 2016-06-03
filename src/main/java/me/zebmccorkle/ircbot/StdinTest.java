package me.zebmccorkle.ircbot;

import java.util.Scanner;

public class StdinTest {
    private static void print(String username, String message) {
        System.out.printf("<%s> %s%n", username, message.replace('\n', ' '));
    }

    public static void main(String[] args) throws User.PermissionException {
        User user = new User("CodingWithClass");
        user.addPermission("op");

        Bot bot = new Bot();
        bot.registerCommand(new PingCommand());
        bot.registerCommand(new EchoCommand());
        bot.registerCommand(new CalcCommand());
        bot.registerCommand(new PermCommand());
        bot.registerCommand(new CharCommand());
        bot.registerCommand(new GetCommand());
        bot.registerCommand(new EvalCommand());

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equals("exit"))
                return;
            print("CodingWithClass", line);
            String[] response = bot.execute(line, user);
            if (response != null)
                for (String i : response)
                    print("ircbot", i);
        }
    }
}
