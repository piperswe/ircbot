package me.zebmccorkle.ircbot;

import javax.script.ScriptException;

import static me.zebmccorkle.kindajs.Globals.eval;

@Name("eval")
@Description("Eval some JavaScript (WARNING: Very insecure, recommended only for debug)")
@Argument(title = "script", required = true)
public class EvalCommand implements ICommand {
    @Override
    public String execute(String[] argv, User user) throws Exception {
        String code = "";
        for (int i = 1; i < argv.length; i++)
            code += " " + argv[i];
        code = code.substring(1);
        try {
            return String.valueOf(eval(code)).replaceAll("(.{250})", "$1\n");
        } catch (ScriptException e) {
            return e.getMessage();
        }
    }

    @Override
    public boolean userCanUse(User user) {
        return user.hasPermission("op") || user.hasPermission("eval");
    }
}
