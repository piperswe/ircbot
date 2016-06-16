package me.zebmccorkle.ircbot.irc;

import me.zebmccorkle.ircbot.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Properties;

public class IRCDriver {
    public static void main(String[] args) throws IOException, User.PermissionException, CertificateException, KeyManagementException, InterruptedException {
        String host = "weber.freenode.net";
        if (args.length >= 2) {
            host = args[1];
        }
        int port = 6697;
        if (args.length >= 3) {
            port = Integer.parseInt(args[2]);
        }
        String nick = "cwcTestingBot";
        if (args.length >= 4) {
            nick = args[3];
        }
        String admin = "zebulon";
        if (args.length >= 5) {
            admin = args[4];
        }
        IRCBot bot = new IRCBot(host, port, nick, admin);
        bot.run();
    }
}
