package me.zebmccorkle.ircbot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String name;
    private List<String> permissions = new ArrayList<>();

    public User(String name) {
        this.name = name;
    }

    /**
     * @return The user's nickname
     */
    public String getName() {
        return name;
    }

    /**
     * Check if a user has a permission
     *
     * @param permission The permission to check if the user has
     * @return true if the user has permission, false otherwise
     */
    public boolean hasPermission(String permission) {
        return permissions.contains(permission.toLowerCase());
    }

    /**
     * Add a permission to the user
     *
     * @param permission Permission to add
     * @throws PermissionException Will be thrown if the user already has the permission
     */
    public void addPermission(String permission) throws PermissionException {
        if (!permissions.contains(permission.toLowerCase()))
            permissions.add(permission.toLowerCase());
        else
            throw new PermissionException(PermissionException.Type.AlreadyExists);
    }

    /**
     * Remove a permission from a user
     *
     * @param permission Permission to remove
     * @throws PermissionException Will be thrown if the user doesn't have the permission
     */
    public void removePermission(String permission) throws PermissionException {
        if (permissions.contains(permission.toLowerCase()))
            permissions.remove(permission.toLowerCase());
        else
            throw new PermissionException(PermissionException.Type.DoesntExist);
    }

    public static class PermissionException extends Exception {
        public Type type;

        public PermissionException(Type type) {
            this.type = type;
        }

        public enum Type {
            AlreadyExists,
            DoesntExist
        }
    }
}
