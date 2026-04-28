package util;

import model.User;

public class SessionUser {

    private static User currentUser;

    private static String token;

    public static void setUser(User user) {
        currentUser = user;
    }

    public static User getUser() {
        return currentUser;
    }

    public static String getRole() {
        if (currentUser != null) {
            return currentUser.getRole();
        }
        return null;
    }

    public static boolean hasRole(String... roles) {
        String userRole = getRole();
        if (userRole == null) return false;
        for (String r : roles) {
            if (userRole.equalsIgnoreCase(r)) return true;
        }
        return false;
    }

    public static void setToken(String t) {
        token = t;
    }

    public static String getToken() {
        return token;
    }

    public static void clear() {
        currentUser = null;
        token = null;
    }
}
