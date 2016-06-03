package me.zebmccorkle.ircbot;

import me.zebmccorkle.kindajs.XMLHttpRequest;

@Name("get")
@Description("HTTP GET a URL")
@Argument(title = "url", required = true)
public class GetCommand implements ICommand {
    @Override
    public String execute(String[] argv, User user) throws Exception {
        String url = "";
        for (int i = 1; i < argv.length; i++)
            url += "%20" + argv[i];
        url = url.substring(3);
        XMLHttpRequest xhr = new XMLHttpRequest();
        xhr.open("GET", argv[1], null, null, null);
        xhr.send();
        while (xhr.readyState != XMLHttpRequest.ReadyState.DONE)
            Thread.sleep(500);
        return xhr.responseText;
    }

    @Override
    public boolean userCanUse(User user) {
        return user.hasPermission("op") || user.hasPermission("get");
    }
}
