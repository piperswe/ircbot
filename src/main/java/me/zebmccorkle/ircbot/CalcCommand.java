package me.zebmccorkle.ircbot;

@Name("calc")
@Description("Calculate an infix expression")
@Argument(title = "Expression", required = true)
public class CalcCommand implements ICommand {
    @Override
    public String execute(String[] argv, User user) throws Exception {
        String expression = "";
        for (int i = 1; i < argv.length; i++) {
            expression += " " + argv[i];
        }
        expression = expression.substring(1);
        return Double.toString(new ExpressionEvaluator(expression, ExpressionEvaluator.EXPRESSIONTYPE.Infix).GetValue());
    }

    @Override
    public boolean userCanUse(User user) {
        return user.hasPermission("op") || user.hasPermission("calc");
    }
}
