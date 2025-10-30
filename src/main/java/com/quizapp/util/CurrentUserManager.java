package com.quizapp.util;

import com.quizapp.entity.User;

public class CurrentUserManager {
    private static User currentUser;
    private static final Object lock = new Object();

    public static User getCurrentUser() {
        synchronized (lock) {
            return currentUser;
        }
    }

    public static void setCurrentUser(User user) {
        synchronized (lock) {
            currentUser = user;
            System.out.println("Current user set: " + (user != null ? user.getUsername() : "null"));
        }
    }

    public static void clearCurrentUser() {
        synchronized (lock) {
            currentUser = null;
            System.out.println("Current user cleared");
        }
    }

    public static boolean isLoggedIn() {
        synchronized (lock) {
            return currentUser != null;
        }
    }

    public static boolean isAdmin() {
        synchronized (lock) {
            return currentUser != null && "ADMIN".equals(currentUser.getRole());
        }
    }

    public static boolean isStudent() {
        synchronized (lock) {
            return currentUser != null && "STUDENT".equals(currentUser.getRole());
        }
    }
}