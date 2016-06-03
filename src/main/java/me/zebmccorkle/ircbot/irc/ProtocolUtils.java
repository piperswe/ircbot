package me.zebmccorkle.ircbot.irc;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProtocolUtils {
    public enum Command {
        PASS,
        NICK,
        USER,
        OPER,
        MODE,
        SERVICE,
        QUIT,
        SQUIT,
        JOIN,
        PART,
        TOPIC,
        NAMES,
        LIST,
        INVITE,
        KICK,
        PRIVMSG,
        NOTICE,
        MOTD,
        LUSERS,
        VERSION,
        STATS,
        LINKS,
        TIME,
        CONNECT,
        TRACE,
        ADMIN,
        INFO,
        SERVLIST,
        SQUERY,
        WHO,
        WHOIS,
        WHOWAS,
        KILL,
        PING,
        PONG,
        ERROR,
        AWAY,
        REHASH,
        DIE,
        RESTART,
        SUMMON,
        USERS,
        WALLOPS,
        USERHOST,
        ISON
    }

    public static class Message {
        @NotNull private String msg;
        private String prefix;
        @NotNull private Object command;
        private List<String> params;

        public Message(@NotNull String msg) {
            this.msg = msg;
            this.prefix = ProtocolUtils.getPrefix(msg);
            this.command = ProtocolUtils.getCommand(msg);
            this.params = ProtocolUtils.getParams(msg);
        }
        public Message(String prefix, @NotNull Command command, List<String> params) {
            this.prefix = prefix;
            this.command = command;
            this.params = params;
            this.msg = createMessage(prefix, command, params);
        }

        public String getMessage() { return msg; }
        public String toString() { return getMessage(); }
        public String getPrefix() { return prefix; }
        public Object getCommand() { return command; }
        public List<String> getParams() { return params; }
        public String getParam(int i) { return params.get(i); }
    }

    public static final String SPACE = "\u0020";
    public static final String NEWLINE = "\r\n";

    public static String toLowercase(@NotNull String str) {
        return str.toLowerCase().replace('{', '[').replace('}', ']').replace('|', '\\').replace('^', '~');
    }
    public static boolean hasPrefix(@NotNull String message) {
        return message.startsWith(":");
    }
    public static String getPrefix(@NotNull String message) {
        if (!hasPrefix(message))
            return null;
        return message.split(SPACE)[0].substring(1).replace(NEWLINE, "");
    }
    public static Object getCommand(@NotNull String message) {
        String command = hasPrefix(message)
            ? message.split(SPACE)[1].replace(NEWLINE, "")
            : message.split(SPACE)[0].replace(NEWLINE, "");
        if (command.matches("[0-9]{3}"))
            return Short.valueOf(command);
        else
            return Command.valueOf(command);
    }
    public static List<String> getParams(@NotNull String message) {
        List<String> list = hasPrefix(message)
            ? Arrays.asList(Arrays.copyOfRange(message.split(SPACE), 2, message.split(SPACE).length))
            : Arrays.asList(Arrays.copyOfRange(message.split(SPACE), 1, message.split(SPACE).length));
        if (list.size() == 0)
            return list;
        List<String> arrayList = new ArrayList<>();
        for (String i : list)
            arrayList.add(i);
        arrayList.add(arrayList.remove(arrayList.size() - 1).replace(NEWLINE, ""));
        return arrayList;
    }
    public static String createMessage(String prefix, @NotNull Command command, Iterable<String> params) {
        if (prefix == null)
            prefix = "";
        else
            prefix = ":" + prefix + " ";
        if (params == null)
            params = new ArrayList<>();

        String paramString = "";
        for (String param : params)
            paramString += " " + param;

        return prefix + command.toString() + paramString + NEWLINE;
    }
}
