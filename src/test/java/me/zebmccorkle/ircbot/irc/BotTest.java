package me.zebmccorkle.ircbot.irc;

import me.zebmccorkle.ircbot.Bot;
import me.zebmccorkle.ircbot.User;
import org.junit.Test;

import java.io.IOException;

public class BotTest {
    @Test
    public void userRegistered() throws IOException {
        Bot bot = new Bot();
        User user = new User("username");
        bot.registerUser(user);
        assert bot.userRegistered(user);
        User user2 = new User("other username");
        bot.registerUser(user2);
        assert bot.userRegistered(user2);
        User user3 = new User("username");
        assert bot.userRegistered(user3);
        User user4 = new User("other other username");
        assert !bot.userRegistered(user4);
    }
}
