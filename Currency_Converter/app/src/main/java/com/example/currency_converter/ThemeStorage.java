package com.example.currency_converter;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeStorage {
    private static final String PREF_NAME = "theme_prefs";
    private static final String KEY_IS_DARK = "is_dark_mode";

    // Saves the theme choice
    public static void saveTheme(Context context, boolean isDark) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putBoolean(KEY_IS_DARK, isDark).apply();
    }

    public static boolean loadTheme(Context context) {
        if (context == null) return false; // Safety check
        SharedPreferences pref = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        return pref.getBoolean("is_dark_mode", false);
    }
}