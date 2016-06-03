package me.zebmccorkle.ircbot;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class Bot {
    private File usersFile = new File("users.dat");
    public Map<String, ICommand> commands = new HashMap<>();
    public char commandCharacter = ':';
    public List<User> users = new ArrayList<>();
    public String nick = "cwcTestingBot";

    /**
     * Create a bot
     */
    public Bot() {
        this.registerCommand(new HelpCommand());

        try {
            readUsers();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a bot with a custom command character
     *
     * @param commandCharacter The character to prefix command names with
     */
    public Bot(char commandCharacter) {
        this.commandCharacter = commandCharacter;
        this.registerCommand(new HelpCommand());

        try {
            readUsers();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a bot with a custom nick
     *
     * @param nick The nickname
     */
    public Bot(String nick) {
        this.nick = nick;
        this.registerCommand(new HelpCommand());

        try {
            readUsers();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a bot with a custom command character <em>and</em> a custom nick
     *
     * @param nick The nickname
     * @param commandCharacter The character to prefix command names with
     */
    public Bot(String nick, char commandCharacter) {
        this.commandCharacter = commandCharacter;
        this.nick = nick;
        this.registerCommand(new HelpCommand());

        try {
            readUsers();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public Bot(File usersFile) {
        this.usersFile = usersFile;
        this.registerCommand(new HelpCommand());

        try {
            readUsers();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Bot(File usersFile, String nick) {
        this.usersFile = usersFile;
        this.nick = nick;
        this.registerCommand(new HelpCommand());

        try {
            readUsers();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Bot(File usersFile, String nick, char commandCharacter) {
        this.usersFile = usersFile;
        this.nick = nick;
        this.registerCommand(new HelpCommand());

        try {
            readUsers();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Bot(File usersFile, char commandCharacter) {
        this.usersFile = usersFile;
        this.commandCharacter = commandCharacter;
        this.registerCommand(new HelpCommand());

        try {
            readUsers();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public User findUser(String name) {
        for (User i : users) {
            if (i.getName().equals(name))
                return i;
        }
        return null;
    }

    public void saveUsers() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(usersFile);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(users);
        out.close();
        fileOut.close();
        System.out.printf("Users persisted to %s%n", usersFile.getName());
    }

    public void readUsers() throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(usersFile);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        users = (List<User>) in.readObject();
        in.close();
        fileIn.close();
        System.out.printf("Users read from %s%n", usersFile.getName());
    }

    /**
     * Add a command to the bot
     *
     * @param command Instance of the command's class
     * @param name    Command to invoke (e.g. if you want users to type "?time", use "time")
     */
    public void registerCommand(ICommand command, String name) {
        for (Field field : command.getClass().getDeclaredFields())
            if (field.isAnnotationPresent(Inject.class)) {
                boolean wasAccessible = field.isAccessible();
                field.setAccessible(true);
                try {
                    if (field.getType().equals(Bot.class))
                        field.set(command, this);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                field.setAccessible(wasAccessible);
            }
        commands.put(name, command);
    }

    /**
     * Add a command to the bot
     *
     * @param command Instance of the command's class
     */
    public void registerCommand(ICommand command) {
        registerCommand(command, CommandUtil.name(command));
    }

    /**
     * Add a user to the bot
     * <p>
     * Usually called when a user first chats
     *
     * @param user The user to check
     */
    public void registerUser(User user) throws IOException {
        users.add(user);
        saveUsers();
    }

    /**
     * Check if a user is registered to the bot
     *
     * @param user The user to check
     * @return Whether or not the user is registered
     */
    public boolean userRegistered(User user) {
        // TODO: Use lambdas or something
        for (User i : users)
            if (i.getName().equals(user.getName()))
                return true;
        return false;
    }

    /**
     * Execute a chat message
     *
     * @param query A random chat message
     * @return A response, each String is a message. null if no command executed.
     */
    public String[] execute(String query, User user) {
        if (!userRegistered(user))
            try {
                registerUser(user);
            } catch (IOException e) {
                e.printStackTrace();
                final User finalUser = user;
                return Arrays.asList(e.toString().split("\n")).stream().map((String x) -> finalUser.getName() + ": " + x).toArray(String[]::new);
            }
        else
            user = findUser(user.getName());

        String[] argv = query.split(" ");
        if (!argv[0].startsWith(Character.toString(commandCharacter)) && !argv[0].startsWith("ircbot"))
            return null;
        else if (argv[0].startsWith(nick))
            return new String[]{user.getName() + ": Yo! Use " + commandCharacter + "help to see what I can do for you."};
        String commandName = argv[0].substring(1);
        if (!commands.containsKey(commandName))
            return new String[]{user.getName() + ": Unknown command: " + commandName};
        ICommand command = commands.get(commandName);
        try {
            if (!command.userCanUse(user))
                return new String[]{user.getName() + ": Unknown command: " + commandName};
            else if (!CommandUtil.enoughArguments(command, argv.length))
                return new String[]{user.getName() + ": Usage: " + commandName + " " + CommandUtil.usage(command)};
            else {
                final User finalUser = user;
                return Arrays.asList(command.execute(argv, user).split("\n")).stream().map((String x) -> finalUser.getName() + ": " + x).toArray(String[]::new);
            }
        } catch (Exception e) {
            e.printStackTrace();
            final User finalUser = user;
            return Arrays.asList(e.toString().split("\n")).stream().map((String x) -> finalUser.getName() + ": " + x).toArray(String[]::new);
        }
    }
}
