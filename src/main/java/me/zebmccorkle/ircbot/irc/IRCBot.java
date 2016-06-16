package me.zebmccorkle.ircbot.irc;

import me.zebmccorkle.ircbot.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.cert.CertificateException;
import java.util.Arrays;

public class IRCBot {
    private Connection connection;
    private Bot bot;

    private String host;
    private int port;
    private String nick;

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

    public IRCBot(String host, int port, String nick, String admin) throws User.PermissionException, IOException {
        this.host = host;
        this.port = port;
        this.nick = nick;
        bot = new Bot();
        bot.registerCommand(new CalcCommand());
        bot.registerCommand(new CharCommand());
        bot.registerCommand(new EchoCommand());
        bot.registerCommand(new EvalCommand());
        bot.registerCommand(new GetCommand());
        bot.registerCommand(new HelpCommand());
        bot.registerCommand(new PermCommand());
        bot.registerCommand(new PingCommand());

        User adminUser = new User(admin);
        adminUser.addPermission("op");
        bot.registerUser(adminUser);
    }

    private static ProtocolUtils.Message send(String message, String to) {
        return new ProtocolUtils.Message(null, ProtocolUtils.Command.NOTICE, Arrays.asList(new String[] { to, ":" + message }));
    }

    public void run() throws CertificateException, IOException, KeyManagementException, InterruptedException {
        connection = new Connection(host, port, true);
        connection.send(new ProtocolUtils.Message(null, ProtocolUtils.Command.NICK, Arrays.asList(new String[]{ nick })));
        connection.send(new ProtocolUtils.Message(null, ProtocolUtils.Command.USER, Arrays.asList(new String[]{ nick, "0", "*", ":" + nick })));
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
    }
}
