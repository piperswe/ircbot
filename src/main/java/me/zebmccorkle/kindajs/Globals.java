package me.zebmccorkle.kindajs;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.*;

public class Globals {
    private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private static Invocable invocable = (Invocable) engine;

    static {
        try {
            engine.eval("XMLHttpRequest = Java.type('me.zebmccorkle.kindajs.XMLHttpRequest')");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public static Object eval(String code) throws ScriptException, NoSuchMethodException {
        ScriptObjectMirror bindings = (ScriptObjectMirror) engine.createBindings();
        return bindings.eval(code);
    }
}
