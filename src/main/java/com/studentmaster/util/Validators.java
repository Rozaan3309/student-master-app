package com.studentmaster.util;

import java.util.regex.Pattern;

public final class Validators {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validators() { }

    public static String studentId(String text) {
        if (text == null || text.isBlank()) return "Student ID is required.";
        try {
            if (Integer.parseInt(text.trim()) <= 0) return "Student ID must be a positive number.";
        } catch (NumberFormatException e) {
            return "Student ID must be a whole number (digits only).";
        }
        return null;
    }

    public static String name(String text) {
        if (text == null || text.isBlank()) return "Name is required.";
        if (text.trim().length() < 2) return "Name must have at least 2 characters.";
        if (text.trim().length() > 100) return "Name must be at most 100 characters.";
        return null;
    }

    public static String email(String text) {
        if (text == null || text.isBlank()) return "Email is required.";
        if (text.trim().length() > 150) return "Email must be at most 150 characters.";
        if (!EMAIL.matcher(text.trim()).matches()) return "Please enter a valid email address (e.g. name@campus.id).";
        return null;
    }
}
