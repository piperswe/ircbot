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
    private static class PRIVMSG {
        public String channel;
        public String message = "";
        public String from;

        public PRIVMSG(ProtocolUtils.Message msg) {
            channel = msg.getParam(0);
            for (int i = 1; i < msg.getParams().size(); i++) {
                message += " " + msg.getParam(i);
            }
            message = message.substring(2);
            from = msg.getPrefix().split("!")[0];
        }
    }

    private static ProtocolUtils.Message send(String message, String to) {
        return new ProtocolUtils.Message(null, ProtocolUtils.Command.NOTICE, Arrays.asList(new String[] { to, ":" + message }));
    }

    public static void main(String[] args) throws IOException, User.PermissionException, CertificateException, KeyManagementException, InterruptedException {
        Connection connection = new Connection("irc.pdgn.co", 6697, true);
        Bot bot = new Bot();
        bot.registerCommand(new CalcCommand());
        bot.registerCommand(new CharCommand());
        bot.registerCommand(new EchoCommand());
        bot.registerCommand(new EvalCommand());
        bot.registerCommand(new GetCommand());
        bot.registerCommand(new HelpCommand());
        bot.registerCommand(new PermCommand());
        bot.registerCommand(new PingCommand());
        connection.send(new ProtocolUtils.Message(null, ProtocolUtils.Command.NICK, Arrays.asList(new String[]{ "cwcTestingBot" })));
        connection.send(new ProtocolUtils.Message(null, ProtocolUtils.Command.USER, Arrays.asList(new String[]{ "cwcTestingBot", "0", "*", ":TestingBot" })));
        //connection.send(new ProtocolUtils.Message(null, ProtocolUtils.Command.JOIN, Arrays.asList(new String[]{ "##cwcTestingBot" })));
        ProtocolUtils.Message msg;
        while ((msg = connection.recv()) != null) {
            System.out.println(msg.toString());
            if (msg.getCommand() == ProtocolUtils.Command.PING)
                connection.send(new ProtocolUtils.Message(null, ProtocolUtils.Command.PONG, msg.getParams()));
            else if (msg.getCommand() == ProtocolUtils.Command.PRIVMSG) {
                PRIVMSG privmsg = new PRIVMSG(msg);
                String[] response = bot.execute(privmsg.message, new User(ProtocolUtils.toLowercase(privmsg.from)));
                if (response != null)
                    for (String message : response)
                        connection.send(send(message, privmsg.channel.startsWith("#") ? privmsg.channel : privmsg.from));
            } else if (msg.getCommand() == ProtocolUtils.Command.INVITE && bot.findUser(ProtocolUtils.toLowercase(msg.getPrefix().split("!")[0])).hasPermission("op"))
                connection.send(new ProtocolUtils.Message(null, ProtocolUtils.Command.JOIN, Arrays.asList(new String[]{ msg.getParam(1) })));
        }
        System.err.println("Disconnected from server");
    }
}
