package com.example.currency_converter;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // ✅ super first

        setContentView(R.layout.activity_settings);

        SwitchMaterial themeSwitch = findViewById(R.id.themeSwitch);

        // ✅ FIXED: Read actual current mode to set toggle state correctly
        boolean isDarkNow = (AppCompatDelegate.getDefaultNightMode()
                == AppCompatDelegate.MODE_NIGHT_YES);
        themeSwitch.setChecked(isDarkNow);

        // ✅ Prevent listener from firing during setChecked above
        themeSwitch.setOnCheckedChangeListener(null);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                ThemeStorage.saveTheme(this, true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                ThemeStorage.saveTheme(this, false);
            }
        });
    }
}