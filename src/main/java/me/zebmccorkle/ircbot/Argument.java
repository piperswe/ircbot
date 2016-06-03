package me.zebmccorkle.ircbot;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Arguments.class)
public @interface Argument {
    String title();

    boolean required() default false;
}

@Retention(RetentionPolicy.RUNTIME)
@interface Arguments {
    Argument[] value();
}