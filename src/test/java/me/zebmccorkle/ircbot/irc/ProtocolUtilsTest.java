package me.zebmccorkle.ircbot.irc;

import org.junit.Test;

import java.util.Arrays;

public class ProtocolUtilsTest {
    @Test
    public void toLowercase() {
        assert ProtocolUtils.toLowercase("ABCD" + ProtocolUtils.NEWLINE).equals("abcd" + ProtocolUtils.NEWLINE);
        assert ProtocolUtils.toLowercase("abcd" + ProtocolUtils.NEWLINE).equals("abcd" + ProtocolUtils.NEWLINE);
        assert ProtocolUtils.toLowercase("AbCd" + ProtocolUtils.NEWLINE).equals("abcd" + ProtocolUtils.NEWLINE);
        assert ProtocolUtils.toLowercase("{}|^" + ProtocolUtils.NEWLINE).equals("[]\\~" + ProtocolUtils.NEWLINE);
    }
    @Test
    public void hasPrefix() {
        assert !ProtocolUtils.hasPrefix("no" + ProtocolUtils.NEWLINE);
        assert ProtocolUtils.hasPrefix(":yes" + ProtocolUtils.NEWLINE);
    }
    @Test
    public void getPrefix() {
        assert ProtocolUtils.getPrefix("no prefix" + ProtocolUtils.NEWLINE) == null;
        assert ProtocolUtils.getPrefix(":has prefix" + ProtocolUtils.NEWLINE).equals("has");
        assert ProtocolUtils.getPrefix(":oneword" + ProtocolUtils.NEWLINE).equals("oneword");
    }
    @Test
    public void getCommand() throws Exception {
        assert ProtocolUtils.getCommand("PRIVMSG" + ProtocolUtils.NEWLINE) == ProtocolUtils.Command.PRIVMSG;
        assert ProtocolUtils.getCommand("PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE) == ProtocolUtils.Command.PRIVMSG;
        assert ProtocolUtils.getCommand(":ztaylor54 PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE) == ProtocolUtils.Command.PRIVMSG;
        assert ((short)ProtocolUtils.getCommand("002 Your host is <servername>, running version <ver>" + ProtocolUtils.NEWLINE)) == 002;
        assert ((short)ProtocolUtils.getCommand("301 <nick> :<away message>" + ProtocolUtils.NEWLINE)) == 301;
        assert ProtocolUtils.getCommand("301 <nick> :<away message>" + ProtocolUtils.NEWLINE) instanceof Short;
        assert ProtocolUtils.getCommand(":ztaylor54 PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE) instanceof ProtocolUtils.Command;
    }
    @Test
    public void createMessage() throws Exception {
        assert ProtocolUtils.createMessage(null, ProtocolUtils.Command.PRIVMSG, Arrays.asList(new String[] { "CodingWithClass", "hi" })).equals("PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE);
        assert ProtocolUtils.createMessage(null, ProtocolUtils.Command.PRIVMSG, null).equals("PRIVMSG" + ProtocolUtils.NEWLINE);
        assert ProtocolUtils.createMessage("ztaylor54", ProtocolUtils.Command.PRIVMSG, Arrays.asList(new String[] { "CodingWithClass", "hi" })).equals(":ztaylor54 PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE);
    }
    @Test
    public void message() {
        assert new ProtocolUtils.Message(null, ProtocolUtils.Command.PRIVMSG, Arrays.asList(new String[] { "CodingWithClass", "hi" })).getMessage().equals("PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE);
        assert new ProtocolUtils.Message("ztaylor54", ProtocolUtils.Command.PRIVMSG, Arrays.asList(new String[] { "CodingWithClass", "hi" })).getMessage().equals(":ztaylor54 PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE);
        assert new ProtocolUtils.Message("PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE).getPrefix() == null;
        assert new ProtocolUtils.Message("PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE).getCommand() == ProtocolUtils.Command.PRIVMSG;
        assert new ProtocolUtils.Message("PRIVMSG" + ProtocolUtils.NEWLINE).getCommand() == ProtocolUtils.Command.PRIVMSG;
        assert new ProtocolUtils.Message("PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE).getParam(0).equals("CodingWithClass");
        assert new ProtocolUtils.Message("PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE).getParam(1).equals("hi");
        assert new ProtocolUtils.Message("PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE).getParams().get(1).equals("hi");
        assert new ProtocolUtils.Message(":ztaylor54 PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE).getPrefix().equals("ztaylor54");
        assert new ProtocolUtils.Message(":ztaylor54 PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE).getCommand() == ProtocolUtils.Command.PRIVMSG;
        assert new ProtocolUtils.Message(":ztaylor54 PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE).getParam(0).equals("CodingWithClass");
        assert new ProtocolUtils.Message(":ztaylor54 PRIVMSG CodingWithClass hi" + ProtocolUtils.NEWLINE).getParam(1).equals("hi");
    }
    @Test
    public void testToString() {
        ProtocolUtils.Message msg = new ProtocolUtils.Message("PRIVMSG CodingWithClass hi");
        assert msg.toString().equals(msg.getMessage());
    }

    @Test
    public void totalCoverage() {
        new ProtocolUtils();
    }
}
